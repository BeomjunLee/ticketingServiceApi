package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.api.ApiAdminController;
import com.hoseo.hackathon.storeticketingservice.api.ApiStoreController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.admin.AdminStoreManageDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * 매장 관리 dto + hateoas link
 */
public class AdminStoreWaitManageResource extends EntityModel<AdminStoreManageDto> {
    public AdminStoreWaitManageResource(AdminStoreManageDto dto, Link... links) {
        super(dto, links);
        add(linkTo(ApiAdminController.class).slash("stores/waiting").withSelfRel());
    }
}
