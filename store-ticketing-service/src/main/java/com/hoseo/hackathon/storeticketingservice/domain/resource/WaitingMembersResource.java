package com.hoseo.hackathon.storeticketingservice.domain.resource;

import com.hoseo.hackathon.storeticketingservice.api.ApiStoreController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.WaitingMembersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class WaitingMembersResource extends EntityModel<WaitingMembersDto> {
    public WaitingMembersResource(WaitingMembersDto dto, Link... links){
        super(dto, links);
        add(linkTo(ApiStoreController.class).slash("tickets").slash(dto.getTicket_id()).slash("hold-ticket").withRel("보류"));
        add(linkTo(ApiStoreController.class).slash("tickets").slash(dto.getTicket_id()).slash("cancel-ticket").withRel("취소"));
        add(linkTo(ApiStoreController.class).slash("tickets").slash(dto.getTicket_id()).slash("check-ticket").withRel("체크"));
    }

}
