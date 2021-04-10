package com.hoseo.hackathon.storeticketingservice.domain.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response {
    @ApiModelProperty(value = "응답 성공여부 : success / fail")
    private ResultStatus result;
    @ApiModelProperty(value = "응답 코드")
    private int status;
    @ApiModelProperty(value = "응답 메세지")
    private String message;

}
