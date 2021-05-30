package com.hoseo.hackathon.storeticketingservice.domain.member.repository;

import com.hoseo.hackathon.storeticketingservice.domain.member.entity.Member;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.MemberListDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.QMemberListDto;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.enums.MemberStatus;
import com.hoseo.hackathon.storeticketingservice.domain.member.repository.condition.MemberSearchCondition;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import static com.hoseo.hackathon.storeticketingservice.domain.QTicket.ticket;
import static org.springframework.util.StringUtils.*;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 매장, 회원 조인 -> member_id, store_id 같은 스토어 객체 한번에 조회
     * @param member_id
     * @return Member Entity
     */
    public Optional<Member> findStoreJoinMember(Long member_id) {
        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.store, store).fetchJoin()
                .where(member.id.eq(member_id))
                .fetchOne();
        return Optional.ofNullable(findMember);
    }

    /**
     * 회원 + 번호표 조인
     * @param username 회원 아이디
     * @return Member Entity
     */
    public Optional<Member> findMemberJoinTicketByUsername(String username) {
        Member findMember = queryFactory
                .selectFrom(member)
                .leftJoin(member.ticketList, ticket).fetchJoin()
                .where(member.username.eq(username))
                .fetchOne();
        return Optional.ofNullable(findMember);
    }

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
                        ticket.id,
                        member.id,
                        member.username,
                        member.name,
                        member.phoneNum,
                        member.email,
                        member.point,
                        member.createdDate
                ))
                .from(member)
                .join(member.ticketList, ticket)
                .where(member.memberStatus.eq(status),
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
