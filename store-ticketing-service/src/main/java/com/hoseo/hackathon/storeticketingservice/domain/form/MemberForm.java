package com.hoseo.hackathon.storeticketingservice.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "일반 회원가입 폼")
public class MemberForm {
    @NotBlank(message = "아이디를 입력해주세요")
    @ApiModelProperty(position = 1, value = "아이디", example = "user")
    private String username;                                //아이디

    @NotBlank(message = "비밀번호를 입력해주세요")
    @ApiModelProperty(position = 2, value = "비밀번호", example = "1234")
    private String password;                                //비밀번호

    @NotBlank(message = "이름을 입력해주세요")
    @ApiModelProperty(position = 3, value = "이름", example = "동길홍")
    private String name;                                    //이름

    @NotBlank(message = "전화번호를 입력해주세요")
    @ApiModelProperty(position =4, value = "전화번호", example = "010xxxxxxxx")
    private String phoneNum;                                //전화번호

    @Email(message = "이메일 형식을 지켜주세요") 
    @NotBlank(message = "이메일을 입력해주세요")
    @ApiModelProperty(position = 5, value = "이메일", example = "naver@naver.com")
    private String email;                                   //이메일
}
