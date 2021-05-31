package com.hoseo.hackathon.storeticketingservice.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
