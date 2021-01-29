package com.hoseo.hackathon.storeticketingservice.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AvgTimeForm {

    @NotNull(message = "1이상 정수만 입력가능합니다")
    @Min(1)
    @ApiModelProperty(position = 1, value = "한사람당 평균 대기시간", example = "5")
    private int avgWaitingTimeByOne;
}
