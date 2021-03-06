package com.hoseo.hackathon.storeticketingservice.domain.admin.controller.hateoas;

import com.hoseo.hackathon.storeticketingservice.domain.admin.controller.AdminController;
import com.hoseo.hackathon.storeticketingservice.domain.store.dto.StoreListDto;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.enums.StoreStatus;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminStoreWaitingToJoinListResource extends EntityModel<StoreListDto> {
    public AdminStoreWaitingToJoinListResource(StoreListDto dto, Link... links) {
        super(dto, links);
        add(linkTo(AdminController.class).slash("storeAdmins").slash(dto.getMember_id()).withRel("매장 관리자 정보보기"));
        if(dto.getStoreStatus() == StoreStatus.VALID)
            add(linkTo(AdminController.class).slash("storeAdmins").slash(dto.getMember_id()).slash("permit-join").withRel("가입 승인"));
        else
            add(linkTo(AdminController.class).slash("storeAdmins").slash(dto.getMember_id()).slash("cancel-join").withRel("가입 취소"));
    }
}
