package com.hoseo.hackathon.storeticketingservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreTicketStatus;
import lombok.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)  //기본 생성자 protected
@Builder
/**
 * 매장 관리 dto
 */
public class StoreManageDto {
    private PagedModel<EntityModel<WaitingMembersDto>> waitingMembers;    //대기회원정보
    private StoreTicketStatus storeTicketStatus;                          //매장 현재상태
    private int totalWaitingCount;                                        //전체 대기인원
    private int totalWaitingTime;                                         //전체 대기시간
    private String notice;                                                //공지사항
    private int avgWaitingTimeByOne;                                      //한사람당 평균 대기시간
//    private int holdMemberCount;                                        //보류회원수

    @JsonIgnore
    private Long store_id;
}
