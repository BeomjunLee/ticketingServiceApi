package com.hoseo.hackathon.storeticketingservice.domain.store.repository;

import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.enums.ErrorStatus;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.enums.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    //가게 이름으로 Store찾기
    Optional<Store> findByName(String name);

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
