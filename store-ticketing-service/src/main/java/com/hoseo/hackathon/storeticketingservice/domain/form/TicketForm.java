package com.hoseo.hackathon.storeticketingservice.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketForm {

    @NotNull(message = "인원 수를 입력해주세요")
    @Min(value = 1)
    @ApiModelProperty(position = 1, value = "인원 수", example = "5")
    private int peopleCount;
}
