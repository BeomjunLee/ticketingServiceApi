package com.hoseo.hackathon.storeticketingservice.domain.store.repository.condition;

import lombok.Data;

@Data
public class StoreSearchCondition {
    private String name;
    private String phoneNum;
    private String address;
}
