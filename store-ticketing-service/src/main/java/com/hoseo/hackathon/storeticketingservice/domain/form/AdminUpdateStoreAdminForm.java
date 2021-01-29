package com.hoseo.hackathon.storeticketingservice.domain.form;

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
public class AdminUpdateStoreAdminForm {
    @NotBlank(message = "아이디를 입력해주세요")
    @ApiModelProperty(position = 1, value = "아이디", example = "updatedStoreAdmin")
    private String member_username;

    @NotBlank(message = "이름을 입력해주세요")
    @ApiModelProperty(position = 2, value = "이름", example = "동길홍")
    private String member_name;

    @NotBlank(message = "전화번호를 입력해주세요")
    @ApiModelProperty(position =3, value = "전화번호", example = "010xxxxxxxx")
    private String member_phoneNum;

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식을 지켜주세요")
    @ApiModelProperty(position = 4, value = "이메일", example = "naver@naver.com")
    private String member_email;


    @NotBlank(message = "가게명을 입력해주세요")
    @ApiModelProperty(position = 5, value = "가게명", example = "updatedㅇㅇ식당")
    private String store_name;
    
    @NotBlank(message = "가게 전화번호를 입력해주세요")
    @ApiModelProperty(position = 6, value = "가게 전화번호", example = "010xxxxxxxx")
    private String store_phoneNum;

    @NotBlank(message = "가게 주소를 입력해주세요")
    @ApiModelProperty(position = 7, value = "가게 주소", example = "경기도 ㅇㅇ시 ㅇㅇ대로 101-101")
    private String store_address;

    @NotBlank(message = "사업자 등록번호를 입력해주세요")
    @ApiModelProperty(position = 8, value = "가게 사업자 등록번호", example = "000-00-00000")
    private String store_companyNumber;
}
