package com.hoseo.hackathon.storeticketingservice.domain.member.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "가게 관리자 회원가입 폼")
public class StoreAdminForm {
    @NotBlank(message = "아이디를 입력해주세요")
    @ApiModelProperty(position = 1, value = "아이디", example = "storeadmin")
    private String memberUsername;                                //아이디

    @NotBlank(message = "비밀번호를 입력해주세요")
    @ApiModelProperty(position = 2, value = "비밀번호", example = "1234")
    private String memberPassword;                                //비밀번호

    @NotBlank(message = "이름을 입력해주세요")
    @ApiModelProperty(position = 3, value = "이름", example = "홍길동")
    private String memberName;                                    //이름

    @NotBlank(message = "전화번호를 입력해주세요")
    @ApiModelProperty(position =4, value = "전화번호", example = "010xxxxxxxx")
    private String memberPhoneNum;                                //전화번호

    @Email(message = "이메일 형식을 지켜주세요")
    @NotBlank(message = "이메일을 입력해주세요")
    @ApiModelProperty(position = 5, value = "이메일", example = "naver@naver.com")
    private String memberEmail;                                   //이메일

    @NotBlank(message = "가게 이름을 입력해주세요")
    @ApiModelProperty(position = 6, value = "가게 명", example = "ㅇㅇ식당")
    private String storeName;                //이름 ->중복되면 안됨

    @NotBlank(message = "가게 전화번호를 입력해주세요")
    @ApiModelProperty(position = 7, value = "가게 전화번호", example = "010xxxxxxxx")
    private String storePhoneNum;            //전화번호
    
    
    @NotNull(message = "주소를 입력해주세요")
    @ApiModelProperty(position = 8, value = "가게 주소", example = "경기도 ㅇㅇ시 ㅇㅇ대로 101-101")
    private String storeAddress;

//    @NotBlank(message = "위도를 입력해주세요")
    @ApiModelProperty(hidden = true)
    private String storeLatitude;            //위도

//    @NotBlank(message = "경도를 입력해주세요")
    @ApiModelProperty(hidden = true)
    private String storeLongitude;           //경도

    @NotBlank(message = "사업자 등록번호를 입력해주세요")
    @ApiModelProperty(position = 9, value = "가게 사업자 등록번호", example = "000-00-00000")
    private String storeCompanyNumber;       //사업자등록번호
}
