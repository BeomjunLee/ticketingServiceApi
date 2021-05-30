package com.hoseo.hackathon.storeticketingservice.domain.member.dto.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateStoreAdminForm {
    @NotBlank(message = "회원 이름을 입력해주세요")
    @ApiModelProperty(position = 1, value = "이름", example = "동길홍")
    private String member_name;

    @NotBlank(message = "회원 전화번호를 입력해주세요")
    @ApiModelProperty(position = 2, value = "전화번호", example = "010xxxxxxxx")
    private String member_phoneNum;

    @NotBlank(message = "회원 이메일을 입력해주세요")
    @Email(message = "이메일 형식을 지켜주세요")
    @ApiModelProperty(position = 3, value = "이메일", example = "naver@naver.com")
    private String member_email;

    @NotBlank(message = "가게 전화번호를 입력해주세요")
    @ApiModelProperty(position = 4, value = "가게 전화번호", example = "010xxxxxxxx")
    private String store_phoneNum;

    @NotBlank(message = "가게 주소를 입력해주세요")
    @ApiModelProperty(position = 5, value = "가게 주소", example = "경기도 ㅇㅇ시 ㅇㅇ대로 101-101")
    private String store_address;
}
