package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.controller.AdminController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminStoreErrorManageByWaitingCountResource extends EntityModel<PagedModel<AdminStoreErrorResource>> {
    public AdminStoreErrorManageByWaitingCountResource(PagedModel<AdminStoreErrorResource> resources) {
        super(resources);
        add(linkTo(AdminController.class).slash("stores/errors/sequence").withSelfRel());   //self
        add(linkTo(AdminController.class).slash("stores/errors").withRel("신청순으로 보기"));
    }
}
