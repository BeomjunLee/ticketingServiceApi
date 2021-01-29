package com.hoseo.hackathon.storeticketingservice.domain.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StoreStatus {
    INVALID("승인 대기"), VALID("승인 완료"), DELETE("삭제 대기");  //승인x, 승인o, 탈퇴

    private String status;
}
