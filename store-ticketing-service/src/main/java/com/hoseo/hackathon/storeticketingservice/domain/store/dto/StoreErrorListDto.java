package com.hoseo.hackathon.storeticketingservice.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreErrorListDto {
    @JsonIgnore
    private Long store_id;
    @JsonIgnore
    private Long member_id;

    private String name;
    private String phoneNum;
    private String address;
    private int totalWaitingCount;
}
