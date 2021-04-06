package com.hoseo.hackathon.storeticketingservice.repository;

import com.hoseo.hackathon.storeticketingservice.domain.QMember;
import com.hoseo.hackathon.storeticketingservice.domain.QStore;
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

import static com.hoseo.hackathon.storeticketingservice.domain.QMember.member;
import static com.hoseo.hackathon.storeticketingservice.domain.QStore.store;
import static com.hoseo.hackathon.storeticketingservice.domain.QTicket.*;

@Repository
@RequiredArgsConstructor
public class TicketQueryRepository {
    private final JPAQueryFactory queryFactory;

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
}
