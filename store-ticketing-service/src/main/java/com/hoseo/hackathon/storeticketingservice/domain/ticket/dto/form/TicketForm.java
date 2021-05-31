package com.hoseo.hackathon.storeticketingservice.domain.ticket.dto.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketForm {

    @NotNull(message = "인원 수를 입력해주세요")
    @Min(value = 1)
    @ApiModelProperty(position = 1, value = "인원 수", example = "5")
    private int peopleCount;
}
