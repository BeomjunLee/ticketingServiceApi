package com.hoseo.hackathon.storeticketingservice.domain.resource;

import com.hoseo.hackathon.storeticketingservice.api.ApiStoreController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreManageDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class WaitingMembersAndStoreManageResource extends EntityModel<StoreManageDto> { //가게 관리 메서드에 사용
    public WaitingMembersAndStoreManageResource(StoreManageDto dto, Link... links){
        super(dto, links);
        add(linkTo(ApiStoreController.class).slash("open-status").withRel("번호표 OPEN"));
        add(linkTo(ApiStoreController.class).slash("close-status").withRel("번호표 CLOSE"));
        add(linkTo(ApiStoreController.class).slash("apply-errors").withRel("오류 접수"));
        add(linkTo(ApiStoreController.class).slash("edit").withRel("매장 정보 수정"));
    }
}
