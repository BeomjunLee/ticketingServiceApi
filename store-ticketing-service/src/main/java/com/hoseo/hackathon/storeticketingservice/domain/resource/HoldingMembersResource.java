package com.hoseo.hackathon.storeticketingservice.domain.resource;

import com.hoseo.hackathon.storeticketingservice.api.ApiStoreController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.HoldingMembersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class HoldingMembersResource extends EntityModel<HoldingMembersDto> {
    public HoldingMembersResource(HoldingMembersDto dto, Link... links){
        super(dto, links);
        add(linkTo(ApiStoreController.class).slash("tickets").slash(dto.getTicket_id()).slash("cancel-holdingTicket").withRel("취소"));
        add(linkTo(ApiStoreController.class).slash("tickets").slash(dto.getTicket_id()).slash("check-holdingTicket").withRel("체크"));
    }

}
