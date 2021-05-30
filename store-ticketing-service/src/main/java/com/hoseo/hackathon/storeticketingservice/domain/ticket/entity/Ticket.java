package com.hoseo.hackathon.storeticketingservice.domain.ticket.entity;
import com.hoseo.hackathon.storeticketingservice.domain.BaseEntity;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.dto.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.Member;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.enums.TicketStatus;
import com.hoseo.hackathon.storeticketingservice.global.common.exception.IsAlreadyCompleteException;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.exception.DuplicateTicketingException;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.exception.IsNotHoldTicketStatusException;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "ticket_id")
    private Long id;

    private int peopleCount;
    private int waitingNum;
    private int waitingTime;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Ticket(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    /**
     * 번호표 발급
     */
    public static Ticket createTicket(TicketForm ticketForm, Store store, Member member) {
        Ticket ticket = Ticket.builder()
                .peopleCount(ticketForm.getPeopleCount())
                .build();

        store.verifyStoreStatus();
        store.verifyStoreTicketStatus();
        ticket.verifyTicket(member);

        int totalWaitingCount = store.getTotalWaitingCount();

        //번호표 세팅
        ticket.changeTicket(ticket.getPeopleCount(),
                totalWaitingCount + 1,
                store.getAvgWaitingTimeByOne() * (totalWaitingCount + 1),
                TicketStatus.VALID);


        ticket.setStore(store);
        ticket.setMember(member);

        store.changeStoreByTicketing(store.getTotalWaitingCount());
        return ticket;
    }

    /**
     * 번호표 취소
     */
    public void cancelTicket() {
        if (getStatus() == TicketStatus.CANCEL)
            throw new IsAlreadyCompleteException("이미 취소처리 되었습니다");

        changeStatusTicket(TicketStatus.CANCEL);
        store.changeStoreByCancelOrNext(store.getTotalWaitingCount());
    }

    /**
     * 번호표 체크
     */
    public void checkTicket() {
        if (getStatus() == TicketStatus.INVALID)
            throw new IsAlreadyCompleteException("이미 체크처리 되었습니다");

        changeStatusTicket(TicketStatus.INVALID);
        store.changeStoreByCancelOrNext(store.getTotalWaitingCount());
    }

    /**
     * 번호표 보류
     */
    public void holdTicket() {
        if (getStatus() == TicketStatus.HOLD)
            throw new IsAlreadyCompleteException("이미 보류처리 되었습니다");
        changeStatusTicket(TicketStatus.HOLD);
        store.changeStoreByCancelOrNext(store.getTotalWaitingCount());
    }

    /**
     *  번호표 검증
     */
    public void verifyTicket(Member member) {
        for(Ticket ticket : member.getTicketList())
            if (ticket.getStatus() == TicketStatus.VALID) {
                throw new DuplicateTicketingException("이미 번호표를 가지고 있습니다");
            }
    }

    //항목 추가 메서드
    public void changeTicket(int peopleCount, int waitingNum, int waitingTime, TicketStatus status) {
        this.peopleCount = peopleCount;
        this.waitingNum = waitingNum;
        this.waitingTime = waitingTime;
        this.status = status;
    }

    //번호표 상태 변경
    public void changeStatusTicket(TicketStatus status) {
        this.status = status;
    }

    //번호표 상태 변경 (보류 번호표만)
    public void changeStatusHoldingTicket(TicketStatus status) {
        if(getStatus() == TicketStatus.HOLD)
            this.status = status;
        else
            throw new IsNotHoldTicketStatusException("보류중인 번호표만 변경할 수 있습니다");
    }

    //==연관관계 편의메서드
    public void setMember(Member member) {
        this.member = member;
        member.getTicketList().add(this);

    }
    public void setStore(Store store) {
        this.store = store;
        store.getTicketList().add(this);
    }


}
