package com.hoseo.hackathon.storeticketingservice.service;

import com.hoseo.hackathon.storeticketingservice.domain.*;
import com.hoseo.hackathon.storeticketingservice.domain.dto.HoldingMembersDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.WaitingMembersDto;
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreInfoForm;
import com.hoseo.hackathon.storeticketingservice.domain.status.ErrorStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreTicketStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.TicketStatus;
import com.hoseo.hackathon.storeticketingservice.exception.*;
import com.hoseo.hackathon.storeticketingservice.repository.MemberRepository;
import com.hoseo.hackathon.storeticketingservice.repository.StoreRepository;
import com.hoseo.hackathon.storeticketingservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final MemberRepository memberRepository;
    private final TicketRepository ticketRepository;
    private final StoreRepository storeRepository;

    /**
     * [회원] 번호표 뽑기
     * @param ticket 번호표 entity
     * @param store_id 매장 고유 id 값
     * @param username 회원 아이디
     * @return created 번호표 entity
     */
    public Ticket createTicket(Ticket ticket, Long store_id, String username) {
        //회원 찾기
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + "에 해당되는 유저를 찾을수 없습니다"));
        //가게 찾기
        Store store = storeRepository.findById(store_id).orElseThrow(() -> new NotFoundStoreException("해당되는 가게를 찾을수 없습니다"));

        if(store.getStoreStatus().equals(StoreStatus.INVALID) || store.getStoreStatus().equals(StoreStatus.DELETE)){//승인되지 않은 매장 체크
            throw new NotAuthorizedStoreException("승인 되지 않은 매장입니다");
        }else if (store.getStoreTicketStatus().equals(StoreTicketStatus.CLOSE)) {  //번호표 발급 활성화 상태 체크
            throw new StoreTicketIsCloseException("번호표 발급이 허용되지 않았습니다");
        }else if (ticketRepository.countByMemberUsernameAndStatus(member.getUsername(), TicketStatus.VALID) > 0
                ||
                ticketRepository.countByMemberUsernameAndStatus(member.getUsername(), TicketStatus.HOLD) > 0) { //번호표 중복 뽑기 체크
            throw new DuplicateTicketingException("이미 번호표를 가지고 있습니다");
        }

        int totalWaitingCount = ticketRepository.countByStore_IdAndStatus(store.getId(), TicketStatus.VALID);

        //번호표 세팅
        ticket.changeTicket(ticket.getPeopleCount(),                                       //인원수(Controller)
                totalWaitingCount + 1,                                          //대기번호
                store.getAvgWaitingTimeByOne() * (totalWaitingCount + 1),       //대기시간
                LocalDateTime.now(),                                                      //발급시간
                TicketStatus.VALID);                                                     //번호표 상태
        ticket.setStore(store);
        ticket.setMember(member);   //연관관계 세팅

        store.changeStoreByTicketing(totalWaitingCount);   //Store 갱신
        return ticketRepository.save(ticket);
    }

    /**
     * [관리자] 번호표 발급 활성화
     * @param username 매장 관리자 아이디
     */
    public void openTicket(String username) {
        Store store = storeRepository.findStoreJoinMemberByUsername(username)
                .orElseThrow(() -> new NotFoundStoreException("관리자의 아이디로 등록된 매장을 찾을수 없습니다"));
        if(store.getStoreStatus().equals(StoreStatus.INVALID) || store.getStoreStatus().equals(StoreStatus.DELETE)){//승인되지 않은 매장 체크
            throw new NotAuthorizedStoreException("승인 되지 않은 매장입니다");
        }else if (store.getStoreTicketStatus().equals(StoreTicketStatus.OPEN)) {
            throw new IsAlreadyCompleteException("이미 활성화 되어있습니다");
        }
        store.changeStoreTicketStatus(StoreTicketStatus.OPEN);
    }

    /**
     * [관리자] 번호표 발급 비활성화
     * @param username 매장 관리자 아이디
     */
    public void closeTicket(String username) {
        Store store = storeRepository.findStoreJoinMemberByUsername(username)
                .orElseThrow(() -> new NotFoundStoreException("관리자의 아이디로 등록된 매장을 찾을수 없습니다"));
        if(store.getStoreStatus().equals(StoreStatus.INVALID) || store.getStoreStatus().equals(StoreStatus.DELETE)){//승인되지 않은 가게 체크
            throw new NotAuthorizedStoreException("승인 되지 않은 매장입니다");
        }else if (store.getStoreTicketStatus().equals(StoreTicketStatus.CLOSE)) {
            throw new IsAlreadyCompleteException("이미 비활성화 되어있습니다");
        }
        store.changeStoreTicketStatus(StoreTicketStatus.CLOSE);
    }

    /**
     * [관리자]번호표 취소
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void cancelTicketByAdmin(String username, Long ticket_id) {

        Store store = storeRepository.findStoreJoinMemberByUsername(username)
                .orElseThrow(() -> new NotFoundStoreException("관리자의 아이디로 등록된 매장을 찾을수 없습니다"));

        Ticket ticket = ticketRepository.findByIdAndStore_Id(ticket_id, store.getId()).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));

        if (ticket.getStatus().equals(TicketStatus.CANCEL)) {
            throw new IsAlreadyCompleteException("이미 취소처리 되었습니다");
        }

        int totalWaitingCount = ticketRepository.countByStore_IdAndStatus(store.getId(), TicketStatus.VALID);

        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), store.getAvgWaitingTimeByOne(), store.getId()); //취소한 번호표보다 뒤에있는 사람들에게 - 1
        ticket.changeStatusTicket(TicketStatus.CANCEL); //번호표 상태 변경
        store.changeStoreByCancelOrNext(totalWaitingCount); //Store 갱신
    }

    /**
     * [회원]번호표 취소
     * @param username 매장 관리자 아이디
     */
    public void cancelTicket(String username) {

        Ticket ticket = ticketRepository.findTicketJoinMemberByUsernameAndStatus(username, TicketStatus.VALID).orElseThrow(() -> new NotFoundTicketException("티켓을 찾을수 없습니다"));
        if (ticket.getStatus().equals(TicketStatus.CANCEL)) {
            throw new IsAlreadyCompleteException("이미 취소처리 되었습니다");
        }

        Store store = storeRepository.findById(ticket.getStore().getId()).orElseThrow(() -> new NotFoundStoreException("해당되는 매장을 찾을수 없습니다"));

        int totalWaitingCount = ticketRepository.countByStore_IdAndStatus(store.getId(), TicketStatus.VALID);

        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), store.getAvgWaitingTimeByOne(), store.getId()); //취소한 번호표보다 뒤에있는 사람들에게 - 1
        ticket.changeStatusTicket(TicketStatus.CANCEL); //번호표 상태 변경
        store.changeStoreByCancelOrNext(totalWaitingCount); //Store 갱신
    }

    /**
     * [관리자] 번호표 넘기기(체크)
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void checkTicket(String username, Long ticket_id) {
        //관리자의 매장 찾기
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));

        //번호표 찾기
        Ticket ticket = ticketRepository.findByIdAndStore_Id(ticket_id, store.getId()).orElseThrow(() -> new NotFoundTicketException("체크할 티켓을 찾을수 없습니다"));
        if (ticket.getStatus().equals(TicketStatus.INVALID)) {
            throw new IsAlreadyCompleteException("이미 체크처리 되었습니다");
        }

        int totalWaitingCount = ticketRepository.countByStore_IdAndStatus(store.getId(), TicketStatus.VALID);

        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), store.getAvgWaitingTimeByOne(), store.getId()); //보류한 번호표보다 뒤에있는 사람들에게 - 1
        ticket.changeStatusTicket(TicketStatus.INVALID); //번호표 상태 변경
        store.changeStoreByCancelOrNext(totalWaitingCount); //Store 갱신
    }

    /**
     * [관리자] 번호표 보류하기
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void holdTicket(String username, Long ticket_id) {
        //관리자의 매장 찾기
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));

        //번호표 찾기
        Ticket ticket = ticketRepository.findByIdAndStore_Id(ticket_id, store.getId())
                .orElseThrow(() -> new NotFoundTicketException("보류할 번호표를 찾을수 없습니다"));
        if (ticket.getStatus().equals(TicketStatus.HOLD)) {
            throw new IsAlreadyCompleteException("이미 보류처리 되었습니다");
        }

        int totalWaitingCount = ticketRepository.countByStore_IdAndStatus(store.getId(), TicketStatus.VALID);

        ticketRepository.updateTicketsMinus1(TicketStatus.VALID, ticket.getWaitingNum(), store.getAvgWaitingTimeByOne(), store.getId()); //보류한 번호표보다 뒤에있는 사람들에게 - 1
        ticket.changeStatusTicket(TicketStatus.HOLD); //번호표 상태 변경
        store.changeStoreByCancelOrNext(totalWaitingCount); //Store 갱신
    }

    /**
     * [관리자]보류한 번호표 체크
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void holdCheckTicket(String username, Long ticket_id) {
        //관리자의 매장 찾기
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));

        Ticket ticket = ticketRepository.findByIdAndStore_Id(ticket_id, store.getId()).orElseThrow(() -> new NotFoundTicketException("체크할 번호표를 찾을수 없습니다"));
        if (ticket.getStatus().equals(TicketStatus.INVALID)) {
            throw new IsAlreadyCompleteException("이미 체크처리 되었습니다");
        }
        
        ticket.changeStatusTicket(TicketStatus.INVALID);//번호표 상태 변경
    }

    /**
     * [관리자] 보류한 번호표 취소
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 고유 id 값
     */
    public void holdCancelTicket(String username, Long ticket_id) {
        //관리자의 매장 찾기
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));

        Ticket ticket = ticketRepository.findByIdAndStore_Id(ticket_id, store.getId()).orElseThrow(() -> new NotFoundTicketException("체크할 번호표를 찾을수 없습니다"));
        if (ticket.getStatus().equals(TicketStatus.CANCEL)) {
            throw new IsAlreadyCompleteException("이미 취소처리 되었습니다");
        }

        ticket.changeStatusTicket(TicketStatus.CANCEL);//번호표 상태 변경
    }

    /**
     * 매장 상태 정보 수정
     * @param username 매장 관리자 아이디
     * @param form 매장 상태 정보 form
     */
    public void updateStoreInfo(String username, StoreInfoForm form) {
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("등록된 매장을 찾을수 없습니다"));
        store.changeNotice(form.getNotice());
        store.changeAvgWaitingTimeByOne(form.getAvgWaitingTimeByOne());
    }

    /**
     * [관리자] 시스템 장애 신청
     * @param username 매장 관리자 아이디
     */
    public void sendErrorSystem(String username) {
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
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
        Ticket ticket = ticketRepository.findTicketJoinMemberByUsernameAndStatus(username, TicketStatus.VALID).orElseThrow(() -> new NotFoundTicketException("번호표를 찾을수 없습니다"));
        return ticket;
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
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));

        if (store.getStoreStatus().equals(StoreStatus.INVALID) || store.getStoreStatus().equals(StoreStatus.DELETE)) {   //매장 승인여부 체크
            throw new NotAuthorizedStoreException("승인되지 않은 매장입니다");
        }

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
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
        if(store.getStoreStatus().equals(StoreStatus.INVALID) || store.getStoreStatus().equals(StoreStatus.DELETE)){//승인되지 않은 매장 체크
            throw new NotAuthorizedStoreException("승인 되지 않은 매장입니다");
        }
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
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));

        if(store.getStoreStatus().equals(StoreStatus.INVALID) || store.getStoreStatus().equals(StoreStatus.DELETE)){//승인되지 않은 가게 체크
            throw new NotAuthorizedStoreException("승인 되지 않은 매장입니다");
        }

        return store;
    }

    /**
     * 매장 관리자 아이디로 Store 찾기(가입 승인 상관x)
     * @param username 매장 관리자 아이디
     * @return 찾은 Store entity
     */
    @Transactional(readOnly = true)
    public Store findStore(String username) {
        Store store = storeRepository.findStoreJoinMemberByUsername(username).orElseThrow(() -> new NotFoundStoreException("관리자의 이름으로 등록된 매장을 찾을수 없습니다"));
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
        if(store.getStoreStatus().equals(StoreStatus.INVALID) || store.getStoreStatus().equals(StoreStatus.DELETE)){//승인되지 않은 가게 체크
            throw new NotAuthorizedStoreException("승인 되지 않은 매장입니다");
        }
        return store;
    }


}
