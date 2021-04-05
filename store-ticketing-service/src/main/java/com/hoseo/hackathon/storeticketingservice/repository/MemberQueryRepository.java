package com.hoseo.hackathon.storeticketingservice.repository;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.QMember;
import com.hoseo.hackathon.storeticketingservice.domain.QTicket;
import com.hoseo.hackathon.storeticketingservice.domain.status.MemberStatus;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hoseo.hackathon.storeticketingservice.domain.QMember.member;
import static com.hoseo.hackathon.storeticketingservice.domain.QTicket.ticket;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Member> findAllByStatus(Pageable pageable, MemberStatus status) {
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .join(member.ticket, ticket).fetchJoin()
                .where(member.status.eq(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Member> content = results.getResults();
        long totalCount = results.getTotal();

        return new PageImpl<>(content, pageable, totalCount);
    }
}
