package com.hoseo.hackathon.storeticketingservice.domain.member.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenForm {
    private String grantType;
    private String refreshToken;
}
