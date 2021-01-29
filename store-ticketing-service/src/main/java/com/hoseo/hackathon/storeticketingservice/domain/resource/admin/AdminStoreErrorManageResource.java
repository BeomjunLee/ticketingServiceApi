package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.api.ApiAdminController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminStoreErrorManageResource extends EntityModel<PagedModel<AdminStoreErrorResource>> {
    public AdminStoreErrorManageResource(PagedModel<AdminStoreErrorResource> resources) {
        super(resources);
        add(linkTo(ApiAdminController.class).slash("stores/errors").withSelfRel());   //self
        add(linkTo(ApiAdminController.class).slash("stores/errors/sequence").withRel("대기 인원 많은순으로 보기"));
    }
}
