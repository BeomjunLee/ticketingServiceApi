package com.hoseo.hackathon.storeticketingservice.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UpdatePasswordForm {
    @NotBlank(message = "현재 비밀번호를 입력해주세요")
    @ApiModelProperty(position = 1, value = "현재 비밀번호", example = "1234")
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요")
    @ApiModelProperty(position = 2, value = "새로운 비밀번호", example = "12345")
    private String newPassword;
}
