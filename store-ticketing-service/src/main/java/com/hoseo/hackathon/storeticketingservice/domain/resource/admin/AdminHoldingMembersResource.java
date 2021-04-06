package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.api.ApiAdminController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.HoldingMembersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminHoldingMembersResource extends EntityModel<HoldingMembersDto> {
    public AdminHoldingMembersResource(HoldingMembersDto dto, Link... links){
        super(dto, links);
        add(linkTo(ApiAdminController.class).slash("stores").slash(dto.getStore_id()).slash("holding").withSelfRel());
    }

}
