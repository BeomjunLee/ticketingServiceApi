package com.hoseo.hackathon.storeticketingservice.domain.member.repository.condition;

import lombok.Data;

@Data
public class MemberSearchCondition {
    private String username;
    private String name;
    private String phoneNum;
    private String orderBy;
}
