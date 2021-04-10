package com.hoseo.hackathon.storeticketingservice.repository.condition;

import lombok.Data;

@Data
public class MemberSearchCondition {
    private String username;
    private String name;
    private String phoneNum;
    private String orderBy;
}
