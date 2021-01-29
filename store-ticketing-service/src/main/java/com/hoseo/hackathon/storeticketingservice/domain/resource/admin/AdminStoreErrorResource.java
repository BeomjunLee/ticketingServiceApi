package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.api.ApiAdminController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreErrorListDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminStoreErrorResource extends EntityModel<StoreErrorListDto> {
    public AdminStoreErrorResource(StoreErrorListDto dto, Link... links) {
        super(dto, links);
        add(linkTo(ApiAdminController.class).slash("stores").slash(dto.getStore_id()).slash("errors").withRel("에러 수정 완료"));
        add(linkTo(ApiAdminController.class).slash("stores").slash(dto.getStore_id()).withRel("가게 번호표 관리"));
        add(linkTo(ApiAdminController.class).slash("stores").slash(dto.getStore_id()).slash("members").slash(dto.getMember_id()).withRel("가게 관리자 정보보기"));
    }
}
