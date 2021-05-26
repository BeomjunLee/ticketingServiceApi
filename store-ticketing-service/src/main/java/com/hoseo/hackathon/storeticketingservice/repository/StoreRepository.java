package com.hoseo.hackathon.storeticketingservice.repository;

import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.status.ErrorStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    //가게 이름으로 Store찾기
    Optional<Store> findByName(String name);

//    //member_id로 select
//    Optional<Store> findByMember_Id(Long member_id);

//    //관리자의 식당 찾기
//    @Query("select s from Store s join fetch s.member m join fetch m.roles where m.username = :username")
//    Optional<Store> findStoreJoinMemberByUsername(@Param("username") String username);

    //가게 목록 보기
    Page<Store> findAllByStoreStatus(StoreStatus storeStatus, Pageable pageable);

    //에러 일어난 가게 목록 보기
    Page<Store> findAllByErrorStatus(ErrorStatus status, Pageable pageable);
    
    //에러 일어난 가게 목록 손님 많은 순으로 보기
    Page<Store> findAllByErrorStatusOrderByTotalWaitingCountDesc(ErrorStatus status, Pageable pageable);

    //등록된 가게 수
    int countByStoreStatus(StoreStatus storeStatus);

    //가게명 중복 방지
    int countByName(String name);

}
