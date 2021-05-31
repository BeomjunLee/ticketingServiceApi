package com.hoseo.hackathon.storeticketingservice.domain.member.dto.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UpdateMemberForm {
    @NotBlank(message = "이름을 입력해주세요")
    @ApiModelProperty(position = 1, value = "이름", example = "동길홍")
    private String name;

    @NotBlank(message = "전화번호를 입력해주세요")
    @ApiModelProperty(position =2, value = "전화번호", example = "010xxxxxxxx")
    private String phoneNum;

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식을 지켜주세요")
    @ApiModelProperty(position = 3, value = "이메일", example = "naver@naver.com")
    private String email;
}
