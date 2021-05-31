package com.hoseo.hackathon.storeticketingservice.domain.store.dto.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvgTimeForm {

    @NotNull(message = "1이상 정수만 입력가능합니다")
    @Min(1)
    @ApiModelProperty(position = 1, value = "한사람당 평균 대기시간", example = "5")
    private int avgWaitingTimeByOne;
}
