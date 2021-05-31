package com.hoseo.hackathon.storeticketingservice.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AdminMemberDto {
    @JsonIgnore
    private Long member_id;
    private String username;
    private String name;
    private String phoneNum;
    private String email;
    private int point;
    private LocalDateTime createdDate;

}