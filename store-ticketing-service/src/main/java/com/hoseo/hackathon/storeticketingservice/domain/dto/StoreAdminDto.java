package com.hoseo.hackathon.storeticketingservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoseo.hackathon.storeticketingservice.domain.status.MemberRole;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StoreAdminDto {
    @JsonIgnore
    private Long member_id;

    private String member_username;
    private String member_name;
    private String member_phoneNum;
    private String member_email;

    @JsonIgnore
    private MemberRole role;
    @JsonIgnore
    private Long store_id;

    private String store_name;
    private String store_phoneNum;
    private String store_address;
    private String store_companyNumber;
    private String store_status;
}
