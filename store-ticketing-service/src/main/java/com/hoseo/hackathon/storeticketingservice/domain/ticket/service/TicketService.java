package com.hoseo.hackathon.storeticketingservice.domain.ticket.service;

import com.hoseo.hackathon.storeticketingservice.global.common.exception.IsAlreadyCompleteException;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.domain.store.exception.NotFoundStoreException;
import com.hoseo.hackathon.storeticketingservice.domain.store.repository.StoreRepository;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.dto.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.Member;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.enums.TicketStatus;
import com.hoseo.hackathon.storeticketingservice.global.common.exception.NotFoundTicketException;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.repository.TicketRepository;
import com.hoseo.hackathon.storeticketingservice.domain.member.repository.MemberQueryRepository;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.repository.TicketQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {
    private final MemberQueryRepository memberQueryRepository;
    private final TicketRepository ticketRepository;
    private final StoreRepository storeRepository;
    private final TicketQueryRepository ticketQueryRepository;


    /**
     * [회원] 번호표 발급
     * @param ticketForm 번호표Form
     * @param store_id 매장 고유 id 값
     * @param username 회원 아이디
     * @return created 번호표 entity
     */
    public Ticket createTicket(TicketForm ticketForm, Long store_id, String username) {
        Member member = memberQueryRepository.findMemberJoinTicketByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + "에 해당되는 유저를 찾을수 없습니다"));
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("해당되는 가게를 찾을수 없습니다"));
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
        ticket.cancelTicket();
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), ticket.getStore().getAvgWaitingTimeByOne(), ticket.getStore().getId());
    }



    /**
     * [관리자]번호표 취소
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void cancelTicket(String username, Long ticket_id) {
        Ticket ticket = ticketQueryRepository.findTicketJoinStore(username, ticket_id).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        ticket.cancelTicket();
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), ticket.getStore().getAvgWaitingTimeByOne(), ticket.getStore().getId());
    }

    /**
     * [관리자] 번호표 넘기기(체크)
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void checkTicket(String username, Long ticket_id) {
        Ticket ticket = ticketQueryRepository.findTicketJoinStore(username, ticket_id).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        ticket.checkTicket();
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), ticket.getStore().getAvgWaitingTimeByOne(), ticket.getStore().getId());
    }

    /**
     * [관리자] 번호표 보류하기
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void holdTicket(String username, Long ticket_id) {
        Ticket ticket = ticketQueryRepository.findTicketJoinStore(username, ticket_id).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        ticket.holdTicket();
        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), ticket.getStore().getAvgWaitingTimeByOne(), ticket.getStore().getId());
    }

    /**
     * [관리자]보류한 번호표 체크
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void holdCheckTicket(String username, Long ticket_id) {
        Ticket ticket = ticketQueryRepository.findTicketJoinStore(username, ticket_id).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        if (ticket.getStatus() == TicketStatus.INVALID)
            throw new IsAlreadyCompleteException("이미 체크처리 되었습니다");

        ticket.changeStatusHoldingTicket(TicketStatus.INVALID);
    }

    /**
     * [관리자] 보류한 번호표 취소
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void holdCancelTicket(String username, Long ticket_id) {
        Ticket ticket = ticketQueryRepository.findTicketJoinStore(username, ticket_id).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        if (ticket.getStatus() == TicketStatus.CANCEL)
            throw new IsAlreadyCompleteException("이미 취소처리 되었습니다");

        ticket.changeStatusHoldingTicket(TicketStatus.CANCEL);
    }


    /**
     * [회원] 자기 티켓 정보 보기
     * @param username 회원 아이디
     * @return 찾은 Ticket entity
     */
    @Transactional(readOnly = true)
    public Ticket findMyTicket(String username) {
        return ticketRepository.findTicketJoinMemberByUsernameAndStatus(username, TicketStatus.VALID)
                .orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));

    }
}
