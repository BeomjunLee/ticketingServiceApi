package com.hoseo.hackathon.storeticketingservice.domain.store.entity.enums;

import lombok.Getter;

@Getter
public enum StoreTicketStatus {
    CLOSE("번호표 뽑기 불가능"), OPEN("번호표 뽑기 가능");

    private String status;

    StoreTicketStatus(String status) {
        this.status = status;
    }
}
