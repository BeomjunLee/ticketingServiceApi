package com.hoseo.hackathon.storeticketingservice.domain.dto.admin;

import com.hoseo.hackathon.storeticketingservice.domain.dto.MemberListDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

@Data
@Builder
/**
 * 회원 관리 dto (admin 용)
 */
public class AdminMemberManageDto {
    private int totalMemberCount;           //전체 회원수
    private int currentUsingServiceCount;   //현재 서비스 이용자수
    private PagedModel<EntityModel<MemberListDto>> memberList;  //회원리스트
}
