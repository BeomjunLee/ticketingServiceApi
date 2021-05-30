package com.hoseo.hackathon.storeticketingservice.domain.member.service;

import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.*;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.Member;
import com.hoseo.hackathon.storeticketingservice.domain.member.exception.DuplicateStoreNameException;
import com.hoseo.hackathon.storeticketingservice.domain.member.exception.DuplicateUsernameException;
import com.hoseo.hackathon.storeticketingservice.domain.member.exception.InvalidRefreshTokenException;
import com.hoseo.hackathon.storeticketingservice.domain.member.exception.RefreshTokenGrantTypeException;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.MemberRole;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.enums.MemberStatus;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.response.LoginResponse;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.domain.member.repository.MemberQueryRepository;
import com.hoseo.hackathon.storeticketingservice.domain.member.repository.MemberRepository;
import com.hoseo.hackathon.storeticketingservice.domain.store.repository.StoreRepository;
import com.hoseo.hackathon.storeticketingservice.global.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberQueryRepository memberQueryRepository;
    private final JwtProvider jwtProvider;


    /**
     * 로그인 요청 회원 찾기
     * @param username 요청 아이디
     * @return 회원 정보 넣은 security User 객체
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username){
        log.info("로그인 요청 회원 찾기");
        Member member = memberRepository.findMemberByUsernameFetch(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " 아이디가 일치하지 않습니다"));

        return new User(member.getUsername(), member.getPassword(), authorities(member.getRoles()));
    }

    private Collection<? extends GrantedAuthority> authorities(Set<MemberRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    /**
     * 회원에게 refreshToken 저장
     * @param username 요청 아이디
     * @param refreshToken refreshToken 값
     */
    @Transactional
    public void findMemberAndSaveRefreshToken(String username, String refreshToken) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " 아이디가 일치하지 않습니다"));
        member.updateRefreshToken(refreshToken);
    }

    /**
     * refreshToken 으로 accessToken 재발급
     * @param refreshTokenForm accessToken 재발급 요청 form
     * @return json response
     */
    @Transactional
    public LoginResponse refreshToken(RefreshTokenForm refreshTokenForm) {
        if (!refreshTokenForm.getGrantType().equals("refreshToken"))
            throw new RefreshTokenGrantTypeException("올바른 grantType 을 입력해주세요");

        Authentication authentication = jwtProvider.getAuthentication(refreshTokenForm.getRefreshToken());

        Member member = memberRepository.findMemberByUsernameAndRefreshToken(authentication.getName(), refreshTokenForm.getRefreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("유효하지 않은 리프레시 토큰입니다"));
        //TODO InvalidRefreshTokenException 예외 Handler

        String accessToken = jwtProvider.generateToken(authentication, false);
        String refreshToken = jwtProvider.generateToken(authentication, true);

        member.updateRefreshToken(refreshToken);

        LoginResponse response = LoginResponse.builder()
                .status(HttpStatus.OK.value())
                .message("accessToken 재발급 성공")
                .accessToken(accessToken)
                .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                .refreshToken(refreshToken)
                .issuedAt(LocalDateTime.now())
                .build();
        return response;
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
    public Member updateMember(String username, UpdateMemberForm memberForm) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당되는 유저를 찾을수 없습니다"));
        member.changeMember(memberForm.getName(), memberForm.getPhoneNum(), memberForm.getEmail());
        return member;
    }
    
    /**
     * 회원 수정(매장 관리자)
     */
    @Transactional
    public Member updateStoreAdmin(String username, UpdateStoreAdminForm storeForm) {
        Member member = memberRepository.findMemberJoinStoreByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당되는 유저를 찾을수 없습니다"));
        member.changeMember(storeForm.getMember_name(), storeForm.getMember_phoneNum(), storeForm.getMember_email());
        member.getStore().changeStore(storeForm.getStore_phoneNum(), storeForm.getStore_address());
        return member;

    }

    /**
     * [회원] 회원가입
     */
    @Transactional
    public Member createMember(MemberForm memberForm) {
        validateDuplicateMember(memberForm.getUsername());
        Member member = Member.createMember(memberForm, passwordEncoder.encode(memberForm.getPassword()));
        return memberRepository.save(member);
    }

    /**
     * [매장 관리자] 회원가입
     */
    @Transactional
    public Member createStoreAdmin(StoreAdminForm storeAdminForm) {
        validateDuplicateMember(storeAdminForm.getMemberUsername());
        validateDuplicateStore(storeAdminForm.getStoreName());

        Store store = Store.createStore(storeAdminForm);
        Member member = Member.createStoreAdmin(storeAdminForm, store, passwordEncoder.encode(storeAdminForm.getMemberPassword()));
        Member savedMember = memberRepository.save(member);
        storeRepository.save(store);
        return savedMember;
    }

    /**
     * [사이트 관리자] 회원가입
     */
    @Transactional
    public void createAdmin(Member member) {
        validateDuplicateMember(member.getUsername());
        member.addRole(MemberRole.ADMIN);
        member.changeMemberStatus(MemberStatus.ADMIN);
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
    }
    /**
     * 중복 매장명 검증
     */
    public void validateDuplicateStore(String name) {
        int findStores = storeRepository.countByName(name);
        if (findStores > 0) {
            throw new DuplicateStoreNameException("매장명이 중복되었습니다");
        }
    }

    /**
     * 회원 정보 보기 (+번호표)
     */
    public Member findByMemberJoinTicketByUsername(String username) {
        return memberQueryRepository.findMemberJoinTicketByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당되는 유저를 찾을수 없습니다"));
    }

    /**
     * 회원 정보 보기 (조인 x)
     */
    public Member findMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당되는 유저를 찾을수 없습니다"));
    }

    /**
     * 매장 회원 정보 보기
     */
    public Member findMemberJoinStoreByUsername(String username) {
        return memberRepository.findMemberJoinStoreByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당되는 유저를 찾을수 없습니다"));
    }

}
