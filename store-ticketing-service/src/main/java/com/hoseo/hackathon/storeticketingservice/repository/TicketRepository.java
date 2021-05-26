package com.hoseo.hackathon.storeticketingservice.repository;

import com.hoseo.hackathon.storeticketingservice.domain.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.status.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByIdAndStore_Id(Long ticket_id, Long store_id);

    //[관리자] 보류, 체크, 취소한 회원들 찾기
    Page<Ticket> findAllByStore_IdAndStatus(Long store_id, TicketStatus status, Pageable pageable);

    //[회원] 티켓 찾기
    @Query("select t from Ticket t join fetch t.member m where m.username = :username and t.status = :status")
    Optional<Ticket> findTicketJoinMemberByUsernameAndStatus(@Param("username") String username, @Param("status") TicketStatus status);

    //현재 서비스 이용자수(번호표를 가지고 있는 인원)
    int countByStatus(TicketStatus status);

    //취소한 사람의 뒤의 티켓을 - 1
    @Transactional
    @Modifying
    @Query("update Ticket t set t.waitingNum = t.waitingNum - 1, t.waitingTime = (t.waitingNum - 1) * :avgWaitingTime " +
            "where t.waitingNum > :waitingNum and t.status = :status and t.store.id = :store_id")
    void updateTicketsMinus1(@Param("status") TicketStatus status, @Param("waitingNum") int waitingNum, @Param("avgWaitingTime") int avgWaitingTime, @Param("store_id")Long store_id);
}
