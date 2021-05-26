package com.hoseo.hackathon.storeticketingservice.service;

import com.hoseo.hackathon.storeticketingservice.domain.*;
import com.hoseo.hackathon.storeticketingservice.domain.dto.HoldingMembersDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.WaitingMembersDto;
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreInfoForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.status.ErrorStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreTicketStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.TicketStatus;
import com.hoseo.hackathon.storeticketingservice.exception.*;
import com.hoseo.hackathon.storeticketingservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final MemberQueryRepository memberQueryRepository;
    private final TicketRepository ticketRepository;
    private final StoreRepository storeRepository;
    private final StoreQueryRepository storeQueryRepository;
    private final TicketQueryRepository ticketQueryRepository;

    /**
     * [회원] 번호표 발급
     * @param ticketForm 번호표Form
     * @param store_id 매장 고유 id 값
     * @param username 회원 아이디
     * @return created 번호표 entity
     */
    public Ticket createTicket(TicketForm ticketForm, Long store_id, String username) {
        //회원 찾기
        Member member = memberQueryRepository.findMemberJoinTicketByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + "에 해당되는 유저를 찾을수 없습니다"));
        //가게 찾기
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("해당되는 가게를 찾을수 없습니다"));
        //번호표 발급
        Ticket ticket = Ticket.createTicket(ticketForm, store, member);

        return ticketRepository.save(ticket);
    }

    /**
     * [회원]번호표 취소
     * @param username 매장 관리자 아이디
     */
    public void cancelTicketByMember(String username) {
        Ticket ticket = ticketRepository.findTicketJoinMemberJoinStoreByUsernameAndStatus(username, TicketStatus.VALID)
                .orElseThrow(() -> new NotFoundTicketException("티켓을 찾을수 없습니다"));
        //번호표 취소
        ticket.cancelTicket();
        //취소한 번호표보다 뒤에있는 사람들에게 - 1
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), ticket.getStore().getAvgWaitingTimeByOne(), ticket.getStore().getId());
    }

    /**
     * [관리자] 번호표 발급 활성화
     * @param username 매장 관리자 아이디
     */
    public void openTicket(String username) {
        Store store = storeQueryRepository.findMemberJoinStoreByUsername(username)
                .orElseThrow(() -> new NotFoundStoreException("관리자의 아이디로 등록된 매장을 찾을수 없습니다"));
       if (store.getStoreTicketStatus() == StoreTicketStatus.OPEN)
            throw new IsAlreadyCompleteException("이미 활성화 되어있습니다");

        store.changeStoreTicketStatus(StoreTicketStatus.OPEN);
    }

    /**
     * [관리자] 번호표 발급 비활성화
     * @param username 매장 관리자 아이디
     */
    public void closeTicket(String username) {
        Store store = storeQueryRepository.findMemberJoinStoreByUsername(username)
                .orElseThrow(() -> new NotFoundStoreException("관리자의 아이디로 등록된 매장을 찾을수 없습니다"));
        if (store.getStoreTicketStatus() == StoreTicketStatus.CLOSE)
            throw new IsAlreadyCompleteException("이미 비활성화 되어있습니다");

        store.changeStoreTicketStatus(StoreTicketStatus.CLOSE);
    }

    /**
     * [관리자]번호표 취소
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void cancelTicket(String username, Long ticket_id) {
        Ticket ticket = ticketQueryRepository.findTicketJoinStore(username, ticket_id).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        //번호표 취소
        ticket.cancelTicket();
        //취소한 번호표보다 뒤에있는 사람들에게 - 1
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), ticket.getStore().getAvgWaitingTimeByOne(), ticket.getStore().getId());
    }

    /**
     * [관리자] 번호표 넘기기(체크)
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void checkTicket(String username, Long ticket_id) {
        //번호표 찾기
        Ticket ticket = ticketQueryRepository.findTicketJoinStore(username, ticket_id).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        //번호표 체크
        ticket.checkTicket();
        //보류한 번호표보다 뒤에있는 사람들에게 - 1
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), ticket.getStore().getAvgWaitingTimeByOne(), ticket.getStore().getId());
    }

    /**
     * [관리자] 번호표 보류하기
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void holdTicket(String username, Long ticket_id) {
        //번호표 찾기
        Ticket ticket = ticketQueryRepository.findTicketJoinStore(username, ticket_id).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        //번호표 보류
        ticket.holdTicket();
        //보류한 번호표보다 뒤에있는 사람들에게 - 1
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), ticket.getStore().getAvgWaitingTimeByOne(), ticket.getStore().getId());
    }

    /**
     * [관리자]보류한 번호표 체크
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void holdCheckTicket(String username, Long ticket_id) {
        //번호표 찾기
        Ticket ticket = ticketQueryRepository.findTicketJoinStore(username, ticket_id).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        if (ticket.getStatus() == TicketStatus.INVALID)
            throw new IsAlreadyCompleteException("이미 체크처리 되었습니다");

        ticket.changeStatusTicket(TicketStatus.INVALID);//번호표 상태 변경
    }

    /**
     * [관리자] 보류한 번호표 취소
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void holdCancelTicket(String username, Long ticket_id) {
        //번호표 찾기
        Ticket ticket = ticketQueryRepository.findTicketJoinStore(username, ticket_id).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        if (ticket.getStatus() == TicketStatus.CANCEL)
            throw new IsAlreadyCompleteException("이미 취소처리 되었습니다");

        ticket.changeStatusTicket(TicketStatus.CANCEL);//번호표 상태 변경
    }

    /**
     * 매장 상태 정보 수정
     * @param username 매장 관리자 아이디
     * @param form 매장 상태 정보 form
     */
    public void updateStoreInfo(String username, StoreInfoForm form) {
        Store store = storeQueryRepository.findMemberJoinStoreByUsername(username).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        store.changeNotice(form.getNotice());
        store.changeAvgWaitingTimeByOne(form.getAvgWaitingTimeByOne());
    }

    /**
     * [관리자] 시스템 장애 신청
     * @param username 매장 관리자 아이디
     */
    public void sendErrorSystem(String username) {
        Store store = storeQueryRepository.findMemberJoinStoreByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
        store.changeErrorStatus(ErrorStatus.ERROR);
    }

//===================================== readOnly = true ====================================//

    /**
     * [회원] 자기 티켓 정보 보기
     * @param username 회원 아이디
     * @return 찾은 Ticket entity
     */
    @Transactional(readOnly = true)
    public Ticket findMyTicket(String username) {
        return ticketRepository.findTicketJoinMemberByUsernameAndStatus(username, TicketStatus.VALID).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));

    }

    /**
     * [관리자] 보류한 번호표들 보기
     * @param username 매장 관리자 아이디
     * @param pageable 페이징
     * @return 페이징 정보가 담긴 HoldingMembers dto
     */
    @Transactional(readOnly = true)
    public Page<HoldingMembersDto> findHoldMembers(String username, Pageable pageable) {
        //관리자의 매장 찾기
        Store store = storeQueryRepository.findMemberJoinStoreByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
        //매장 승인 상태 검증
        store.verifyStoreStatus();

        Page<Ticket> tickets = ticketRepository.findAllByStore_IdAndStatus(store.getId(), TicketStatus.HOLD, pageable);
        return tickets.map(ticket -> HoldingMembersDto.builder()
                .name(ticket.getMember().getName())
                .phoneNum(ticket.getMember().getPhoneNum())
                .ticket_id(ticket.getId())
                .build());
    }

    /**
     * [관리자] 대기중인 회원들 보기 (보류 대기표는 x)
     * @param username 매장 관리자 아이디
     * @param pageable 페이징
     * @return 페이징 처리된 WaitingMembers dto
     */
    @Transactional(readOnly = true)
    public Page<WaitingMembersDto> findWaitingMembers(String username, Pageable pageable) {
        //관리자의 매장 찾기
        Store store = storeQueryRepository.findMemberJoinStoreByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
        //매장 승인 상태 검증
        store.verifyStoreStatus();

        Page<Ticket> tickets = ticketRepository.findAllByStore_IdAndStatus(store.getId(), TicketStatus.VALID, pageable);
        return tickets.map(ticket -> WaitingMembersDto.builder()
                .waitingNum(ticket.getWaitingNum())
                .name(ticket.getMember().getName())
                .phoneNum(ticket.getMember().getPhoneNum())
                .ticket_id(ticket.getId())
                .build());
    }

    /**
     * 매장 관리자 아이디로 Store 찾기(가입 승인 매장만)
     * @param username 매장 관리자 아이디
     * @return 찾은 Store entity
     */
    @Transactional(readOnly = true)
    public Store findValidStore(String username) {
        Store store = storeQueryRepository.findMemberJoinStoreByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
        //매장 승인 상태 검증
        store.verifyStoreStatus();

        return store;
    }

    /**
     * 매장 관리자 아이디로 Store 찾기(가입 승인 상관x)
     * @param username 매장 관리자 아이디
     * @return 찾은 Store entity
     */
    @Transactional(readOnly = true)
    public Store findStore(String username) {
        Store store = storeQueryRepository.findMemberJoinStoreByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));

        return store;
    }

    /**
     * 매장 고유 id 값으로 Store 찾기
     * @param id 매장 고유 id 값
     * @return 찾은 Store entity
     */
    @Transactional(readOnly = true)
    public Store findValidStoreById(Long id) {
        Store store = storeRepository.findById(id).orElseThrow(() -> new NotFoundStoreException("매장을 찾을수 없습니다"));
        //매장 승인 상태 검증
        store.verifyStoreStatus();

        return store;
    }


}
