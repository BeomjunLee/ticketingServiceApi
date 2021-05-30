package com.hoseo.hackathon.storeticketingservice.domain.store.controller.hateoas;

import com.hoseo.hackathon.storeticketingservice.domain.store.controller.StoreController;
import com.hoseo.hackathon.storeticketingservice.domain.store.dto.WaitingMembersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class WaitingMembersResource extends EntityModel<WaitingMembersDto> {
    public WaitingMembersResource(WaitingMembersDto dto, Link... links){
        super(dto, links);
        add(linkTo(StoreController.class).slash("tickets").slash(dto.getTicket_id()).slash("hold-ticket").withRel("보류"));
        add(linkTo(StoreController.class).slash("tickets").slash(dto.getTicket_id()).slash("cancel-ticket").withRel("취소"));
        add(linkTo(StoreController.class).slash("tickets").slash(dto.getTicket_id()).slash("check-ticket").withRel("체크"));
    }

}
