package com.hoseo.hackathon.storeticketingservice.domain.member.dto.form;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenForm {
    private String grantType;
    private String refreshToken;
}
