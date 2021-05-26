package com.hoseo.hackathon.storeticketingservice.domain;

import com.hoseo.hackathon.storeticketingservice.domain.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.status.TicketStatus;
import com.hoseo.hackathon.storeticketingservice.exception.IsAlreadyCompleteException;
import lombok.*;

import javax.jdo.annotations.Unique;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "ticket_id")
    private Long id;

    private int peopleCount;             //인원수
    private int waitingNum;             //대기번호
    private int waitingTime;            //대기시간
    private LocalDateTime createdDate;   //발급시간

    @Enumerated(EnumType.STRING)
    private TicketStatus status;        //티켓 유효 상태(valid, invalid, cancel)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;                //store_id

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

        store.verifyStoreStatus();  //승인되지 않은 매장 체크
        store.verifyStoreTicketStatus();//번호표 발급 활성화 상태 체크
        member.verifyTicket();//번호표 중복 뽑기 체크

        int totalWaitingCount = store.getTotalWaitingCount();

        //번호표 세팅
        ticket.changeTicket(ticket.getPeopleCount(),                                       //인원수(Controller)
                totalWaitingCount + 1,                                          //대기번호
                store.getAvgWaitingTimeByOne() * (totalWaitingCount + 1),       //대기시간
                LocalDateTime.now(),                                                      //발급시간
                TicketStatus.VALID);                                                     //번호표 상태


        ticket.setStore(store);
        ticket.setMember(member);   //연관관계 세팅

        store.changeStoreByTicketing(store.getTotalWaitingCount());   //Store 갱신
        return ticket;
    }

    /**
     * 번호표 취소
     */
    public void cancelTicket() {
        if (getStatus() == TicketStatus.CANCEL)
            throw new IsAlreadyCompleteException("이미 취소처리 되었습니다");

        changeStatusTicket(TicketStatus.CANCEL); //번호표 상태 변경
        store.changeStoreByCancelOrNext(store.getTotalWaitingCount()); //Store 갱신
    }

    /**
     * 번호표 체크
     */
    public void checkTicket() {
        if (getStatus() == TicketStatus.INVALID)
            throw new IsAlreadyCompleteException("이미 체크처리 되었습니다");

        changeStatusTicket(TicketStatus.INVALID); //번호표 상태 변경
        store.changeStoreByCancelOrNext(store.getTotalWaitingCount()); //Store 갱신
    }

    /**
     * 번호표 보류
     */
    public void holdTicket() {
        if (getStatus() == TicketStatus.HOLD)
            throw new IsAlreadyCompleteException("이미 보류처리 되었습니다");
        changeStatusTicket(TicketStatus.HOLD); //번호표 상태 변경
        store.changeStoreByCancelOrNext(store.getTotalWaitingCount()); //Store 갱신
    }

    //항목 추가 메서드
    public void changeTicket(int peopleCount, int waitingNum, int waitingTime, LocalDateTime createdDate, TicketStatus status) {
        this.peopleCount = peopleCount;
        this.waitingNum = waitingNum;
        this.waitingTime = waitingTime;
        this.createdDate = createdDate;
        this.status = status;
    }
    

    //==연관관계 편의메서드
    public void setMember(Member member) {
        this.member = member;
        member.getTicketList().add(this);

    }
    //==연관관계 편의메서드
    public void setStore(Store store) {
        this.store = store;
        store.getTicketList().add(this);
    }

    //==비지니스 로직==
    //번호표 상태 변경
    public void changeStatusTicket(TicketStatus status) {
        this.status = status;
    }

}
