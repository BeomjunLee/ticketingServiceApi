package com.hoseo.hackathon.storeticketingservice.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class HoldingMembersDto {
    private String name;
    private String phoneNum;
    @JsonIgnore
    private Long ticket_id;
    @JsonIgnore
    private Long store_id;
}
