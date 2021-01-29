package com.hoseo.hackathon.storeticketingservice.domain.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoseo.hackathon.storeticketingservice.domain.status.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)  //기본 생성자 protected
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