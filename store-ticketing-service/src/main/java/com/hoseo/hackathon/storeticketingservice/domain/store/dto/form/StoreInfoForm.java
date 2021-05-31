package com.hoseo.hackathon.storeticketingservice.domain.store.dto.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * 매장 상태정보 수정
 */
public class StoreInfoForm {
    @NotBlank(message = "한글자 이상 입력해주세요")
    @ApiModelProperty(position = 1, value = "공지사항", example = "재료가 소진되었습니다")
    private String notice;

    @NotNull(message = "1이상 정수만 입력가능합니다")
    @Min(1)
    @ApiModelProperty(position = 1, value = "한사람당 평균 대기시간", example = "5")
    private int avgWaitingTimeByOne;
}
