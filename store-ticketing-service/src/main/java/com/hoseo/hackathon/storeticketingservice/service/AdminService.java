package com.hoseo.hackathon.storeticketingservice.service;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.dto.*;
import com.hoseo.hackathon.storeticketingservice.domain.form.AdminUpdateMemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.AdminUpdateStoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreInfoForm;
import com.hoseo.hackathon.storeticketingservice.domain.status.*;
import com.hoseo.hackathon.storeticketingservice.exception.*;
import com.hoseo.hackathon.storeticketingservice.repository.*;
import com.hoseo.hackathon.storeticketingservice.repository.condition.MemberSearchCondition;
import com.hoseo.hackathon.storeticketingservice.repository.condition.StoreSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final MemberRepository memberRepository;
    private final TicketRepository ticketRepository;
    private final StoreRepository storeRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final StoreQueryRepository storeQueryRepository;
    private final TicketQueryRepository ticketQueryRepository;

//===============================================매장 관리=============================================

    /**
     * 상태별 매장 목록 (매장이름, 매장 전화번호, 매장 주소 검색기능)
     * @param storeStatus 매장 상태
     * @param pageable 페이징
     * @return 페이징 처리된 StoreList dto
     */
    public Page<StoreListDto> findStores(StoreStatus storeStatus, Pageable pageable, StoreSearchCondition condition) {
        return storeQueryRepository.findAllByStoreStatus(storeStatus, pageable, condition);
    }

    /**
     * 가입 승인된 매장 수
     * @return 가입 승인된 매장 수
     */
    public int totalEnrollStoreCount() {
        return storeRepository.countByStoreStatus(StoreStatus.VALID);
    }

    /**
     * 매장 관리자 정보 보기
     * @param member_id 회원 고유 id 값
     * @return 찾은 Member entity
     */
    public Member findStoreAdmin(Long member_id) {
        return memberRepository.findById(member_id).orElseThrow(() -> new NoSuchElementException("매장 관리자를 찾을수 없습니다"));
    }

//=================================매장 번호표 관리 기능=======================================

    /**
     * 번호표 발급 활성화
     * @param store_id 매장 고유 id 값
     */
    @Transactional
    public void openTicket(Long store_id) {
        Store store = storeRepository.findById(store_id)
                .orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        if (store.getStoreTicketStatus() == StoreTicketStatus.OPEN)
            throw new IsAlreadyCompleteException("이미 활성화 되어있습니다");
        store.changeStoreTicketStatus(StoreTicketStatus.OPEN);
    }

    /**
     * 번호표 발급 비활성화
     * @param store_id 매장 고유 id 값
     */
    @Transactional
    public void closeTicket(Long store_id) {
        Store store = storeRepository.findById(store_id)
                .orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        if (store.getStoreTicketStatus() == StoreTicketStatus.CLOSE)
            throw new IsAlreadyCompleteException("이미 비활성화 되어있습니다");
        store.changeStoreTicketStatus(StoreTicketStatus.CLOSE);
    }

    /**
     * [관리자]번호표 취소
     * @param store_id 매장 고유 id 값
     * @param ticket_id 번호표 고유 id 값
     */
    @Transactional
    public void cancelTicketByAdmin(Long store_id, Long ticket_id) {
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        Ticket ticket = ticketRepository.findById(ticket_id).orElseThrow(() -> new NotFoundTicketException("티켓을 찾을수 없습니다"));
        //번호표 취소
        ticket.cancelTicket(store);
        //취소한 번호표보다 뒤에있는 사람들에게 - 1
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), store.getAvgWaitingTimeByOne(), store.getId());
    }

    /**
     * [회원]번호표 취소
     * @param ticket_id 번호표 고유 id 값
     */
    @Transactional
    public void cancelTicket(Long ticket_id) {

        Ticket ticket = ticketRepository.findById(ticket_id).orElseThrow(() -> new NotFoundTicketException("티켓을 찾을수 없습니다"));
        Store store = storeRepository.findById(ticket.getStore().getId()).orElseThrow(() -> new NotFoundStoreException("해당되는 매장 찾을수 없습니다"));
        //번호표 취소
        ticket.cancelTicket(store);
        //취소한 번호표보다 뒤에있는 사람들에게 - 1
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), store.getAvgWaitingTimeByOne(), store.getId());
    }

    /**
     * 번호표 체크
     * @param store_id 매장 고유 id 값
     * @param ticket_id 번호표 고유 id 값
     */
    @Transactional
    public void checkTicket(Long store_id, Long ticket_id) {
        //번호표 찾기
        Ticket ticket = ticketRepository.findById(ticket_id).orElseThrow(() -> new NotFoundTicketException("체크할 티켓을 찾을수 없습니다"));
        //관리자의 매장 찾기
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        //번호표 체크
        ticket.checkTicket(store);
        //보류한 번호표보다 뒤에있는 사람들에게 - 1
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), store.getAvgWaitingTimeByOne(), store.getId());
    }


    /**
     * 번호표 보류
     * @param store_id 매장 고유 id 값
     * @param ticket_id 번호표 고유 id 값
     */
    @Transactional
    public void holdTicket(Long store_id, Long ticket_id) {
        //번호표 찾기
        Ticket ticket = ticketRepository.findById(ticket_id).orElseThrow(() -> new NotFoundTicketException("보류할 티켓을 찾을수 없습니다"));
        //관리자의 매장 찾기
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        //번호표 보류
        ticket.holdTicket(store);
        //보류한 번호표보다 뒤에있는 사람들에게 - 1
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), store.getAvgWaitingTimeByOne(), store.getId());
    }

    /**
     * 보류한 번호표 체크
     * @param store_id 매장 고유 id 값
     * @param ticket_id 번호표 고유 id 값
     */
    @Transactional
    public void holdCheckTicket(Long store_id, Long ticket_id) {
        Ticket ticket = ticketRepository.findByIdAndStore_Id(ticket_id, store_id).orElseThrow(() -> new NotFoundTicketException("체크할 티켓을 찾을수 없습니다"));
        if (ticket.getStatus().equals(TicketStatus.INVALID))
            throw new IsAlreadyCompleteException("이미 체크처리 되었습니다");

        ticket.changeStatusTicket(TicketStatus.INVALID);//번호표 상태 변경
    }

    /**
     * 보류한 번호표 취소
     * @param store_id 매장 고유 id 값
     * @param ticket_id 번호표 고유 id 값
     */
    @Transactional
    public void holdCancelTicket(Long store_id, Long ticket_id) {
        Ticket ticket = ticketRepository.findByIdAndStore_Id(ticket_id, store_id).orElseThrow(() -> new NotFoundTicketException("체크할 티켓을 찾을수 없습니다"));
        if (ticket.getStatus().equals(TicketStatus.CANCEL))
            throw new IsAlreadyCompleteException("이미 취소처리 되었습니다");

        ticket.changeStatusTicket(TicketStatus.CANCEL);//번호표 상태 변경
    }

    /**
     * 보류한 회원들 + 번호표 보기
     * @param store_id 매장 고유 id 값
     * @param pageable 페이징
     * @return 페이징 처리된 HoldingMembers dto
     */
    public Page<HoldingMembersDto> findHoldMembers(Long store_id, Pageable pageable) {
        //관리자의 매장 찾기
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        //매장 승인 상태 검증
        store.verifyStoreStatus();

        Page<Ticket> tickets = ticketRepository.findAllByStore_IdAndStatus(store.getId(), TicketStatus.HOLD, pageable);
        return tickets.map(ticket -> HoldingMembersDto.builder()
                .name(ticket.getMember().getName())
                .phoneNum(ticket.getMember().getPhoneNum())
                .ticket_id(ticket.getId())
                .store_id(store.getId())
                .build());
    }

    /**
     * 대기중인 회원들 + 번호표 보기
     * @param store_id 매장 고유 id 값
     * @param pageable 페이징
     * @return 페이징 처리된 WaitingMembers dto
     */
    public Page<WaitingMembersDto> findWaitingMembers(Long store_id, Pageable pageable) {

        Page<Ticket> tickets = ticketQueryRepository.findTickets(store_id, TicketStatus.VALID, pageable);
        return tickets.map(ticket -> WaitingMembersDto.builder()
                .waitingNum(ticket.getWaitingNum())
                .name(ticket.getMember().getName())
                .phoneNum(ticket.getMember().getPhoneNum())
                .ticket_id(ticket.getId())
                .store_id(store_id)
                .build());
    }

    /**
     * 매장 상태 정보 수정
     * @param store_id 매장 고유 id 값
     * @param form 매장 상태 정보 form
     */
    @Transactional
    public void updateStoreInfo(Long store_id, StoreInfoForm form) {
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        store.changeNotice(form.getNotice());
        store.changeAvgWaitingTimeByOne(form.getAvgWaitingTimeByOne());
    }

    /**
     * 매장, 관리자정보 수정
     * @param store_id 매장 고유 id 값
     * @param member_id 매장 관리자 고유 id 값
     * @param form 매장, 관리자정보 수정 form
     */
    @Transactional
    public void updateStoreAdmin(Long store_id, Long member_id, AdminUpdateStoreAdminForm form) {
        Member member = memberRepository.findById(member_id).orElseThrow(() -> new UsernameNotFoundException("해당되는 회원을 찾을수 없습니다"));
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));

        if(!member.getUsername().equalsIgnoreCase(form.getMember_username())){//수정할 아이디가 중복이 아닐경우
            validateDuplicateMember(form.getMember_username());
        }
        //포인트는 서비스 미적용
        member.changeMemberByAdmin(form.getMember_username(), form.getMember_name(), form.getMember_phoneNum(), form.getMember_email(), member.getPoint());

        if (!store.getName().equalsIgnoreCase(form.getStore_name())) {//수정할 매장이 중복이 아닐경우
            validateDuplicateStore(form.getStore_name());
        }
        store.changeStoreByAdmin(form.getStore_name(), form.getStore_phoneNum(), form.getStore_address(), form.getStore_companyNumber());
    }

    /**
     * 중복 매장명 검증
     * @param name 매장명
     */
    public void validateDuplicateStore(String name) {
        int findStores = storeRepository.countByName(name);
        if (findStores > 0) {
            throw new DuplicateStoreNameException("가게명이 중복되었습니다");
        }
        //두 유저가 동시에 가입할 경우를 대비해서 DB 에도 유니크 제약조건을 걸어줘야함
    }

    /**
     * 매장 정보 찾기(승인되지 않은 매장도)
     * @param store_id 매장 고유 id 값
     * @return 찾은 Store entity
     */
    public Store findStore(Long store_id) {
        return storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
    }

//==========================================================회원 관리===============================================================

    /**
     * 회원 리스트 보기
     * @param pageable 페이징
     * @param status 회원 상태
     * @return 페이징 처리된 MemberList dto
     */
    public Page<MemberListDto> findMembers(Pageable pageable, MemberStatus status, MemberSearchCondition condition) {
        return memberQueryRepository.findAllByStatus(pageable, status, condition);
    }

    /**
     * 가입승인된 전체 회원 수
     * @return 가입 승인된 전체 회원 수
     */
    public int totalMemberCount() {
        return memberRepository.countByStatus(MemberStatus.VALID);
    }

    /**
     * 현재 서비스 이용자 수(번호표 뽑은)
     * @return 현재 서비스 이용자 수
     */
    public int currentUsingServiceCount() {
        return ticketRepository.countByStatus(TicketStatus.VALID);
    }

    /**
     * 회원 정보 찾기
     * @param member_id 회원 고유 id 값
     * @return
     */
    public Member findMember(Long member_id) {
        return memberRepository.findById(member_id).orElseThrow(() -> new UsernameNotFoundException("해당되는 회원을 찾을수 없습니다"));
    }

    /**
     * 회원 수정
     * @param member_id 회원 고유 id 값
     * @param form 회원 수정 form
     */
    @Transactional
    public void updateMember(Long member_id, AdminUpdateMemberForm form) {
        Member member = memberRepository.findById(member_id).orElseThrow(() -> new UsernameNotFoundException("해당되는 회원을 찾을수 없습니다"));

        if(!member.getUsername().equalsIgnoreCase(form.getUsername())){//수정할 아이디가 중복이 아닐경우
            validateDuplicateMember(form.getUsername());
        }
        member.changeMemberByAdmin(form.getUsername(), form.getName(), form.getPhoneNum(), form.getEmail(), form.getPoint());
    }

    /**
     * 중복 회원 검증
     * @param username 회원 입력 아이디
     */
    public void validateDuplicateMember(String username) {
        int findMembers = memberRepository.countByUsername(username);
        if (findMembers > 0) {
            throw new DuplicateUsernameException("아이디가 중복되었습니다");
        }
        //두 유저가 동시에 가입할 경우를 대비해서 DB 에도 유니크 제약조건을 걸어줘야함
    }

    /**
     * 회원 탈퇴(username 은 null, 회원 상태는 DELETE)
     * (가게 관리자인경우 가게 이름은 null, 가게 상태도 DELETE)
     * @param member_id 회원 고유 id 값
     */
    @Transactional
    public void deleteMember(Long member_id) {
        Member member = memberRepository.findById(member_id).orElseThrow(() -> new UsernameNotFoundException("해당되는 회원을 찾을수 없습니다"));
        if (member.getRoles().contains(MemberRole.USER)) {
            member.changeMemberStatus(MemberStatus.DELETE);
        } else if (member.getRoles().contains(MemberRole.STORE_ADMIN)) {
            Store store = storeRepository.findByMember_Id(member_id).orElseThrow(() -> new NotFoundStoreException("등록된 가게를 찾을수 없습니다"));
            member.changeMemberStatus(MemberStatus.DELETE);
            store.changeStoreStatus(StoreStatus.DELETE);
        }
    }

    /**
     * 탈퇴후 7일지난 회원 영구삭제
     */
    @Transactional
    public void deleteMemberWeekPast() {
        List<Member> memberList = memberRepository.findAllByStatus(MemberStatus.DELETE);
        if(memberList == null) throw new NoSuchElementException("삭제할 회원이 없습니다");
        memberList.stream().forEach(member ->{
            Period period = Period.between(member.getDeletedDate().toLocalDate(), LocalDateTime.now().toLocalDate());
                if (period.getDays() >= 7) {
                    memberRepository.delete(member);
                    //TODO 성능 개선 한방 쿼리 작업 필요
                }
        });
    }

//============================================가입 승인====================================================

    /**
     * 매장 관리자 가입 승인 (시간넣어야됨)
     * @param member_id 회원 고유 id 값
     * @param store_id 매장 고유 id 값
     */
    @Transactional
    public void permitStoreAdmin(Long member_id, Long store_id) {
        Store store = storeQueryRepository.findStoreJoinMember(member_id, store_id).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        store.permitStoreAdmin();
    }

    /**
     * 매장 관리자 가입 승인 취소
     * @param member_id 회원 고유 id 값
     * @param store_id 매장 고유 id 값
     */
    @Transactional
    public void cancelPermitStoreAdmin(Long member_id, Long store_id) {
        Store store = storeQueryRepository.findStoreJoinMember(member_id, store_id).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        store.cancelPermitStoreAdmin();
    }


//===================================시스템 장애=======================================

    /**
     *  시스템 오류난 매장 리스트 보기
     * @param pageable 페이징
     * @return 페이징 처리된 StoreErrorList dto
     */
    public Page<StoreErrorListDto> findErrorStores(Pageable pageable) {
        Page<Store> stores = storeRepository.findAllByErrorStatus(ErrorStatus.ERROR, pageable);
        return stores.map(store -> StoreErrorListDto.builder()
                .store_id(store.getId())
                .member_id(store.getMember().getId())
                .name(store.getName())
                .phoneNum(store.getPhoneNum())
                .address(store.getAddress())
                .totalWaitingCount(store.getTotalWaitingCount())
                .build());
    }

    /**
     * 시스템 장애 손님수많은 순서대로 보기
     * @param pageable 페이징
     * @return 페이징 처리된 StoreErrorList dto
     */
    //TODO Querydsl 로 동적 쿼리 합치기
    public Page<StoreErrorListDto> findErrorStoresByTotalWaitingCount(Pageable pageable) {
        Page<Store> stores = storeRepository.findAllByErrorStatusOrderByTotalWaitingCountDesc(ErrorStatus.ERROR, pageable);
        return stores.map(store -> StoreErrorListDto.builder()
                .store_id(store.getId())
                .member_id(store.getMember().getId())
                .name(store.getName())
                .phoneNum(store.getPhoneNum())
                .address(store.getAddress())
                .totalWaitingCount(store.getTotalWaitingCount())
                .build());
    }

    /**
     * 시스템 오류 신청
     * @param store_id 매장 고유 id 값
     */
    @Transactional
    public void sendErrorSystem(Long store_id) {
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
        store.changeErrorStatus(ErrorStatus.ERROR);
    }

    /**
     * 시스템 오류 해결 상태s -> 완료
     * @param store_id 매장 고유 id 값
     */
    @Transactional
    public void completeErrorSystem(Long store_id) {
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
        store.changeErrorStatus(ErrorStatus.GOOD);
    }

}
