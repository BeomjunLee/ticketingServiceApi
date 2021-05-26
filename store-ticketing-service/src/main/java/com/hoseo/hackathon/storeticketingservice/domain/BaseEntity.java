package com.hoseo.hackathon.storeticketingservice.domain;

import lombok.Getter;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BaseEntity {
    private LocalDateTime createdDate;

    public void changeCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
