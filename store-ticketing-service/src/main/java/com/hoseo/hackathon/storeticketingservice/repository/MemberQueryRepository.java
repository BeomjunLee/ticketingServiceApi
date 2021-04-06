package com.hoseo.hackathon.storeticketingservice.repository;

import com.hoseo.hackathon.storeticketingservice.domain.dto.MemberListDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.QMemberListDto;
import com.hoseo.hackathon.storeticketingservice.domain.status.MemberStatus;
import com.hoseo.hackathon.storeticketingservice.repository.condition.MemberSearchCondition;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hoseo.hackathon.storeticketingservice.domain.QMember.member;
import static com.hoseo.hackathon.storeticketingservice.domain.QTicket.ticket;
import static org.springframework.util.StringUtils.*;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 회원 리스트 검색 (아이디, 이름, 전화번호)
     * @param pageable 페이징
     * @param status 회원 가입 상태
     * @param condition 검색 기능
     * @return 페이징 처리된 MemberList dto
     */
    public Page<MemberListDto> findAllByStatus(Pageable pageable, MemberStatus status, MemberSearchCondition condition) {

        QueryResults<MemberListDto> results = queryFactory
                .select(new QMemberListDto(
                        member.ticket.id,
                        member.id,
                        member.username,
                        member.name,
                        member.phoneNum,
                        member.email,
                        member.point,
                        member.createdDate
                ))
                .from(member)
                .join(member.ticket, ticket)
                .where(member.status.eq(status),
                        usernameContain(condition.getUsername()),
                        nameContain(condition.getName()),
                        phoneContain(condition.getPhoneNum()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MemberListDto> content = results.getResults();
        long totalCount = results.getTotal();

        return new PageImpl<>(content, pageable, totalCount);
    }

    private BooleanExpression usernameContain(String username) {
        if(hasText(username))
            return member.username.contains(username);
        return null;
    }

    private BooleanExpression nameContain(String name) {
        if(hasText(name))
            return member.name.contains(name);
        return null;
    }

    private BooleanExpression phoneContain(String phoneNum) {
        if(hasText(phoneNum))
            return member.phoneNum.contains(phoneNum);
        return null;
    }
}
