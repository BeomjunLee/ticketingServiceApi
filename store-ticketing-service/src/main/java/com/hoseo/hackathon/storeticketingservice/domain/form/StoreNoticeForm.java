package com.hoseo.hackathon.storeticketingservice.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StoreNoticeForm {
    @NotBlank(message = "한글자 이상 입력해주세요")
    @ApiModelProperty(position = 1, value = "공지사항", example = "재료가 소진되었습니다")
    private String notice;

}
