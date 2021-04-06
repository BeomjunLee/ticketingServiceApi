package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.api.ApiAdminController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.WaitingMembersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminWaitingMembersResource extends EntityModel<WaitingMembersDto> {
    public AdminWaitingMembersResource(WaitingMembersDto dto, Link... links){
        super(dto, links);
        add(linkTo(ApiAdminController.class).slash("stores").slash(dto.getStore_id()).slash("hold-ticket").slash(dto.getTicket_id()).withRel("보류"));
        add(linkTo(ApiAdminController.class).slash("stores").slash(dto.getStore_id()).slash("cancel-ticket").slash(dto.getTicket_id()).withRel("취소"));
        add(linkTo(ApiAdminController.class).slash("stores").slash(dto.getStore_id()).slash("check-ticket").slash(dto.getTicket_id()).withRel("체크"));
    }

}
