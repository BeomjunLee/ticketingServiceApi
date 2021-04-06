package com.hoseo.hackathon.storeticketingservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
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

    @QueryProjection
    public StoreListDto(Long store_id, Long member_id, String name, String phoneNum, String address, LocalDateTime createdDate, String companyNumber) {
        this.store_id = store_id;
        this.member_id = member_id;
        this.name = name;
        this.phoneNum = phoneNum;
        this.address = address;
        this.createdDate = createdDate;
        this.companyNumber = companyNumber;
    }
}
