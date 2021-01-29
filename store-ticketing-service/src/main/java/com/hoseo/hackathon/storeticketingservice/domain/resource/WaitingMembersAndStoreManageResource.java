package com.hoseo.hackathon.storeticketingservice.domain.resource;

import com.hoseo.hackathon.storeticketingservice.api.ApiStoreController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreManageDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class WaitingMembersAndStoreManageResource extends EntityModel<StoreManageDto> { //가게 관리 메서드에 사용
    public WaitingMembersAndStoreManageResource(StoreManageDto dto, Link... links){
        super(dto, links);
        add(linkTo(ApiStoreController.class).slash("status").withRel("번호표 OPEN"));
        add(linkTo(ApiStoreController.class).slash("status").withRel("번호표 CLOSE"));
        add(linkTo(ApiStoreController.class).slash("errors").withRel("오류 접수"));
        add(linkTo(ApiStoreController.class).slash("--").withRel("공지사항 수정"));
        add(linkTo(ApiStoreController.class).slash("--").withRel("한사람당 대기시간 수정"));
    }
}
