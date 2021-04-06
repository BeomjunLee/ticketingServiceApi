package com.hoseo.hackathon.storeticketingservice.repository;

import com.hoseo.hackathon.storeticketingservice.domain.dto.QStoreListDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreListDto;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreStatus;
import com.hoseo.hackathon.storeticketingservice.repository.condition.StoreSearchCondition;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hoseo.hackathon.storeticketingservice.domain.QMember.member;
import static com.hoseo.hackathon.storeticketingservice.domain.QStore.store;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 매장 리스트 검색 (이름, 전화번호, 주소)
     * @param storeStatus 매장 승인 상태
     * @param pageable 페이징
     * @param condition 매장 검색 조건
     * @return 페이징 처리된 StoreList dto
     */
    public Page<StoreListDto> findAllByStoreStatus(StoreStatus storeStatus, Pageable pageable, StoreSearchCondition condition) {
        QueryResults<StoreListDto> results = queryFactory
                .select(new QStoreListDto(
                        store.id,
                        store.member.id,
                        store.name,
                        store.phoneNum,
                        store.address,
                        store.createdDate,
                        store.companyNumber
                ))
                .from(store)
                .join(store.member, member)
                .where(store.storeStatus.eq(storeStatus),
                        nameContain(condition.getName()),
                        phoneNumContain(condition.getPhoneNum()),
                        addressContain(condition.getAddress()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<StoreListDto> content = results.getResults();
        long totalCount = results.getTotal();

        return new PageImpl<>(content, pageable, totalCount);
    }

    private BooleanExpression nameContain(String name) {
        if(hasText(name))
            return store.name.contains(name);
        return null;
    }

    private BooleanExpression phoneNumContain(String phoneNum) {
        if(hasText(phoneNum))
            return store.phoneNum.contains(phoneNum);
        return null;
    }

    private BooleanExpression addressContain(String address) {
        if(hasText(address))
            return store.address.contains(address);
        return null;
    }
}