package com.hoseo.hackathon.storeticketingservice.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.MemberRole;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminStoreAdminDto {
    @JsonIgnore
    private Long member_id;

    private String member_username;
    private String member_name;
    private String member_phoneNum;
    private String member_email;
    private LocalDateTime member_createdDate;

    @JsonIgnore
    private MemberRole role;
    @JsonIgnore
    private Long store_id;

    private String store_name;
    private String store_phoneNum;
    private String store_address;
    private String store_companyNumber;
    private LocalDateTime store_createdDate;
}
