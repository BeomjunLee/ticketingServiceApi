package com.hoseo.hackathon.storeticketingservice.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.enums.StoreStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * 매장 리스트 dto
 */
public class StoreListDto {
    @JsonIgnore
    private Long store_id;
    @JsonIgnore
    private Long member_id;

    private String name;
    private String phoneNum;
    private String address;
    private LocalDateTime createdDate;
    private String companyNumber;
    private StoreStatus storeStatus;

    @QueryProjection
    public StoreListDto(Long store_id, Long member_id, String name, String phoneNum, String address, LocalDateTime createdDate, String companyNumber, StoreStatus storeStatus) {
        this.store_id = store_id;
        this.member_id = member_id;
        this.name = name;
        this.phoneNum = phoneNum;
        this.address = address;
        this.createdDate = createdDate;
        this.companyNumber = companyNumber;
        this.storeStatus = storeStatus;
    }
}
