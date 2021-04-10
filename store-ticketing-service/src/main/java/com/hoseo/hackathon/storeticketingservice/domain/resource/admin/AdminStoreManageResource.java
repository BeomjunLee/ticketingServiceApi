package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.controller.StoreController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.admin.AdminStoreManageDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * 매장 관리 + hateoas link (admin 용)
 */
public class AdminStoreManageResource extends EntityModel<AdminStoreManageDto> {
    public AdminStoreManageResource(AdminStoreManageDto dto, Link... links) {
        super(dto, links);
        add(linkTo(StoreController.class).slash("stores").withSelfRel());
    }
}
