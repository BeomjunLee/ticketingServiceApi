package com.hoseo.hackathon.storeticketingservice.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateMemberForm {
    @NotBlank(message = "아이디를 입력해주세요")
    @ApiModelProperty(position = 1, value = "아이디", example = "updatedUser")
    private String username;

    @NotBlank(message = "이름을 입력해주세요")
    @ApiModelProperty(position = 2, value = "이름", example = "동길홍")
    private String name;

    @NotBlank(message = "전화번호를 입력해주세요")
    @ApiModelProperty(position =3, value = "전화번호", example = "010xxxxxxxx")
    private String phoneNum;

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식을 지켜주세요")
    @ApiModelProperty(position = 4, value = "이메일", example = "naver@naver.com")
    private String email;

    @NotNull(message = "포인트를 입력해주세요")
    @ApiModelProperty(position = 5, value = "포인트", example = "300")
    private int point;
}
