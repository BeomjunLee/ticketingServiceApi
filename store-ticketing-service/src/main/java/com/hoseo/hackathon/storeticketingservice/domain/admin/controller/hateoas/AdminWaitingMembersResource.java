package com.hoseo.hackathon.storeticketingservice.domain.admin.controller.hateoas;

import com.hoseo.hackathon.storeticketingservice.domain.admin.controller.AdminController;
import com.hoseo.hackathon.storeticketingservice.domain.store.dto.WaitingMembersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminWaitingMembersResource extends EntityModel<WaitingMembersDto> {
    public AdminWaitingMembersResource(WaitingMembersDto dto, Link... links){
        super(dto, links);
        add(linkTo(AdminController.class).slash("tickets").slash(dto.getTicket_id()).slash("hold-ticket").withRel("보류"));
        add(linkTo(AdminController.class).slash("tickets").slash(dto.getTicket_id()).slash("cancel-ticket").withRel("취소"));
        add(linkTo(AdminController.class).slash("tickets").slash(dto.getTicket_id()).slash("check-ticket").withRel("체크"));
    }

}
