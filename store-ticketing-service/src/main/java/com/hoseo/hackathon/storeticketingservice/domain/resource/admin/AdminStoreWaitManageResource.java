package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.api.ApiStoreController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.admin.AdminStoreManageDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminStoreWaitManageResource extends EntityModel<AdminStoreManageDto> {
    public AdminStoreWaitManageResource(AdminStoreManageDto dto, Link... links) {
        super(dto, links);
        add(linkTo(ApiStoreController.class).slash("stores/wait").withSelfRel());
        add(linkTo(ApiStoreController.class).slash("stores/wait").withRel("검색"));
        //TODO 검색 링크추가
    }
}
