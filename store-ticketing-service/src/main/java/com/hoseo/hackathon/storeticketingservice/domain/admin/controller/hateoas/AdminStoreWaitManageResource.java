package com.hoseo.hackathon.storeticketingservice.domain.admin.controller.hateoas;

import com.hoseo.hackathon.storeticketingservice.domain.admin.controller.AdminController;
import com.hoseo.hackathon.storeticketingservice.domain.admin.dto.AdminStoreManageDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * 매장 관리 dto + hateoas link
 */
public class AdminStoreWaitManageResource extends EntityModel<AdminStoreManageDto> {
    public AdminStoreWaitManageResource(AdminStoreManageDto dto, Link... links) {
        super(dto, links);
        add(linkTo(AdminController.class).slash("stores/waiting").withSelfRel());
    }
}
