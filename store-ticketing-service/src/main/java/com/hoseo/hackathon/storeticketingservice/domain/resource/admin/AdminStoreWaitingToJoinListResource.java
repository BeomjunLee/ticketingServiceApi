package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.api.ApiAdminController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreListDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminStoreWaitingToJoinListResource extends EntityModel<StoreListDto> {
    public AdminStoreWaitingToJoinListResource(StoreListDto dto, Link... links) {
        super(dto, links);
        add(linkTo(ApiAdminController.class).slash("stores").slash(dto.getStore_id()).slash("members").slash(dto.getMember_id()).withRel("가게 관리자 정보보기"));
        add(linkTo(ApiAdminController.class).slash("stores").slash(dto.getStore_id()).slash("authorization").slash(dto.getMember_id()).withRel("가입 승인"));
    }
}
