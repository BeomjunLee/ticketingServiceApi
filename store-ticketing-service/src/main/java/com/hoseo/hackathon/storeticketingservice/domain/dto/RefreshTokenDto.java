package com.hoseo.hackathon.storeticketingservice.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenDto {
    private String grant_type;
}
