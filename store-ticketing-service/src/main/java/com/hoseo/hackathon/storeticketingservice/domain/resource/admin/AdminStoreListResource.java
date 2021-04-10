package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.controller.AdminController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreListDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminStoreListResource extends EntityModel<StoreListDto> {
    public AdminStoreListResource(StoreListDto dto, Link... links) {
        super(dto, links);
        add(linkTo(AdminController.class).slash("stores")
                .slash(dto.getStore_id())
                .slash("members")
                .slash(dto.getMember_id())
                .slash("cancel-join")
                .withRel("매장 승인 취소"));
        add(linkTo(AdminController.class).slash("stores").slash(dto.getStore_id()).withRel("매장 대기 번호표 관리"));
        add(linkTo(AdminController.class).slash("stores").slash(dto.getStore_id()).slash("members").slash(dto.getMember_id()).withRel("매장 관리자 정보보기"));
    }
}
