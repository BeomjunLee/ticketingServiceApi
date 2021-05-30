package com.hoseo.hackathon.storeticketingservice.domain.store.service;

import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.global.common.exception.IsAlreadyCompleteException;
import com.hoseo.hackathon.storeticketingservice.domain.store.dto.HoldingMembersDto;
import com.hoseo.hackathon.storeticketingservice.domain.store.dto.WaitingMembersDto;
import com.hoseo.hackathon.storeticketingservice.domain.store.dto.form.StoreInfoForm;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.enums.ErrorStatus;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.enums.StoreTicketStatus;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.enums.TicketStatus;
import com.hoseo.hackathon.storeticketingservice.domain.store.exception.NotFoundStoreException;
import com.hoseo.hackathon.storeticketingservice.domain.store.repository.StoreRepository;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.repository.TicketRepository;
import com.hoseo.hackathon.storeticketingservice.domain.store.repository.StoreQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreQueryRepository storeQueryRepository;
    private final TicketRepository ticketRepository;

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

    /**
     * [관리자] 보류한 번호표들 보기
     * @param username 매장 관리자 아이디
     * @param pageable 페이징
     * @return 페이징 정보가 담긴 HoldingMembers dto
     */
    @Transactional(readOnly = true)
    public Page<HoldingMembersDto> findHoldMembers(String username, Pageable pageable) {
        Store store = storeQueryRepository.findMemberJoinStoreByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
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
        Store store = storeQueryRepository.findMemberJoinStoreByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
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
        store.verifyStoreStatus();

        return store;
    }

}
