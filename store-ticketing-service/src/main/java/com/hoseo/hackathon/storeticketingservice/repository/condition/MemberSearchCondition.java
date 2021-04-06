package com.hoseo.hackathon.storeticketingservice.repository.condition;

import com.hoseo.hackathon.storeticketingservice.domain.status.MemberStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.Role;
import lombok.Data;

@Data
public class MemberSearchCondition {
    private String username;
    private String name;
    private String phoneNum;
    private String orderBy;
}
