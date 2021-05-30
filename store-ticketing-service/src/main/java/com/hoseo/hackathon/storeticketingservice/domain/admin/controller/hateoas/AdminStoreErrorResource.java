package com.hoseo.hackathon.storeticketingservice.domain.admin.controller.hateoas;

import com.hoseo.hackathon.storeticketingservice.domain.admin.controller.AdminController;
import com.hoseo.hackathon.storeticketingservice.domain.store.dto.StoreErrorListDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminStoreErrorResource extends EntityModel<StoreErrorListDto> {
    public AdminStoreErrorResource(StoreErrorListDto dto, Link... links) {
        super(dto, links);
        add(linkTo(AdminController.class).slash("stores").slash(dto.getStore_id()).slash("complete-errors").withRel("오류 수정 완료"));
        add(linkTo(AdminController.class).slash("stores").slash(dto.getStore_id()).withRel("매장 번호표 관리"));
        add(linkTo(AdminController.class).slash("storeAdmins").slash(dto.getMember_id()).withRel("매장 관리자 정보보기"));
    }
}
