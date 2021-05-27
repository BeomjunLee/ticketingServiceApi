package com.hoseo.hackathon.storeticketingservice.repository.querydsl;
import com.hoseo.hackathon.storeticketingservice.domain.QTicket;
import com.hoseo.hackathon.storeticketingservice.domain.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.status.TicketStatus;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import static com.hoseo.hackathon.storeticketingservice.domain.QMember.member;
import static com.hoseo.hackathon.storeticketingservice.domain.QStore.store;
import static com.hoseo.hackathon.storeticketingservice.domain.QTicket.*;

@Repository
@RequiredArgsConstructor
public class TicketQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 번호표 상태별로 번호표 조회 + 페이징
     * @param store_id 매장 pk
     * @param status 번호표 상태
     * @param pageable 페이징
     * @return Page<Ticket>
     */
    public Page<Ticket> findTickets(Long store_id, TicketStatus status, Pageable pageable) {
        QueryResults<Ticket> results = queryFactory
                .select(ticket)
                .from(ticket)
                .join(ticket.member, member)
                .fetchJoin()
                .where(ticket.status.eq(status),
                      ticket.store.id.eq(store_id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Ticket> content = results.getResults();
        long totalCount = results.getTotal();

        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * [매장 관리자] 번호표 조회
     * @param username 매장 관리자 아이디
     * @param ticket_id 번호표 pk
     * @return Ticket entity
     */
    public Optional<Ticket> findTicketJoinStore(String username, Long ticket_id) {
        Ticket ticket = queryFactory
                .select(QTicket.ticket)
                .from(QTicket.ticket)
                .join(QTicket.ticket.store, store).fetchJoin()
                .join(store.memberList, member)
                .where(QTicket.ticket.id.eq(ticket_id),
                        member.username.eq(username))
                .fetchOne();
        return Optional.ofNullable(ticket);
    }

    /**
     * [사이트 관리자] 번호표 조회
     * @param ticket_id 번호표 pk
     * @return Ticket entity
     */
    public Optional<Ticket> findTicketJoinStore(Long ticket_id) {
        Ticket ticket = queryFactory
                .select(QTicket.ticket)
                .from(QTicket.ticket)
                .join(QTicket.ticket.store, store).fetchJoin()
                .where(QTicket.ticket.id.eq(ticket_id))
                .fetchOne();
        return Optional.ofNullable(ticket);
    }
}
