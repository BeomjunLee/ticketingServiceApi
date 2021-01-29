package com.hoseo.hackathon.storeticketingservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyTicketDto {
    private String name;                //가게 이름
    private String phoneNum;            //가게 전화번호
    private String notice;              //Store의 공지사항
    private int totalWaitingCount;       //전체 대기인원

    private int peopleCount;             //인원수
    private int waitingNum;             //대기번호
    private int waitingTime;            //대기시간
}
