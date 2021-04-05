package com.hoseo.hackathon.storeticketingservice.service;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.form.UpdateMemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.UpdateStoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.status.*;
import com.hoseo.hackathon.storeticketingservice.exception.*;
import com.hoseo.hackathon.storeticketingservice.repository.MemberRepository;
import com.hoseo.hackathon.storeticketingservice.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true) //조회최적화
@RequiredArgsConstructor    //스프링 주입
public class MemberService{

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인체크
     */
    public Member loginCheck(String username, String password) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "에 해당되는 유저를 찾을수 없습니다"));
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치 하지않습니다");
        } else return member;
    }

    /**
     * 로그인 성공시 refreshToken 저장 및 갱신
     */
    @Transactional
    public void updateRefreshToken(String username, String refreshToken) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "에 해당되는 유저를 찾을수 없습니다"));
        member.changeRefreshToken(refreshToken);
    }

    /**
     * DB에 저장된 refreshToken 값
     */
    public boolean checkRefreshToken(String username, String refreshToken) {
        String findRefreshToken = memberRepository.findRefreshToken(username).orElseThrow(() -> new NotFoundRefreshTokenException("회원의 refresh_token을 찾을 수 없습니다"));
        if (refreshToken.equals(findRefreshToken)) return true;
        else throw new NotMatchedRefreshTokenException("요청된 refresh_token이 회원의 refresh_token과 일치하지 않습니다");
    }


    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "에 해당되는 유저를 찾을수 없습니다"));
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치 하지않습니다");
        }
        member.encodingPassword(passwordEncoder.encode(newPassword));
    }

    /**
     * 회원 수정(일반)
     */
    @Transactional
    public void updateMember(String username, UpdateMemberForm memberForm) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당되는 유저를 찾을수 없습니다"));
        member.changeMember(memberForm.getName(), memberForm.getPhoneNum(), memberForm.getEmail());
    }
    
    /**
     * 회원 수정(가게 관리자)
     */
    @Transactional
    public void updateStoreAdmin(String username, UpdateStoreAdminForm storeForm) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당되는 유저를 찾을수 없습니다"));
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("해당되는 가게를 찾을수 없습니다"));
        member.changeMember(storeForm.getMember_name(), storeForm.getMember_phoneNum(), storeForm.getMember_email());
        store.changeStore(storeForm.getStore_phoneNum(), storeForm.getStore_address());

    }

    /**
     * [회원] 회원가입
     */
    @Transactional //조회가 아니므로 Transactional
    public Member createMember(Member member) {
        validateDuplicateMember(member.getUsername()); //중복회원검증
        member.changeRole(Role.USER);   //권한부여
        member.changeMemberStatus(MemberStatus.VALID);  //일반 회원은 바로 가입
        //비밀번호 encoding
        member.encodingPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }

    /**
     * [관리자] 회원가입
     */
    @Transactional //조회가 아니므로 Transactional
    public void createStoreAdmin(Member member, Store store) {
        validateDuplicateMember(member.getUsername()); //중복회원검증
        validateDuplicateStore(store.getName());       //중복 가게명 검증

        member.changeRole(Role.STORE_ADMIN); //권한부여
        member.changeMemberStatus(MemberStatus.INVALID); //가게 관리자는 가입 대기상태
        //비밀번호 encoding
        member.encodingPassword(passwordEncoder.encode(member.getPassword()));

        store.changeErrorStatus(ErrorStatus.GOOD);
        store.changeStoreTicketStatus(StoreTicketStatus.CLOSE); //번호표 발급 비활성화
        store.changeStoreStatus(StoreStatus.INVALID);  //승인 대기

        memberRepository.save(member);
        storeRepository.save(store);
    }

    /**
     * 회원 탈퇴
     */

    /**
     * [사이트 관리자] 회원가입
     */
    @Transactional //조회가 아니므로 Transactional
    public void createAdmin(Member member) {
        validateDuplicateMember(member.getUsername()); //중복회원검증
        member.changeRole(Role.ADMIN); //권한부여
        member.changeMemberStatus(MemberStatus.ADMIN); //가게 관리자는 가입 대기상태
        //비밀번호 encoding
        member.encodingPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
    }

    /**
     * 중복 회원 검증
     */
    public void validateDuplicateMember(String username) {
        int findMembers = memberRepository.countByUsername(username);
        if (findMembers > 0) {
            throw new DuplicateUsernameException("아이디가 중복되었습니다");
        }
        //두 유저가 동시에 가입할 경우를 대비해서 DB 에도 유니크 제약조건을 걸어줘야함
    }
    /**
     * 중복 가게명 검증
     */
    public void validateDuplicateStore(String name) {
        int findStores = storeRepository.countByName(name);
        if (findStores > 0) {
            throw new DuplicateStoreNameException("가게명이 중복되었습니다");
        }
        //두 유저가 동시에 가입할 경우를 대비해서 DB 에도 유니크 제약조건을 걸어줘야함
    }

    /**
     * 회원 정보 보기
     */
    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당되는 유저를 찾을수 없습니다"));
    }

    /**
     * 포인트 기부하기
     */
}
