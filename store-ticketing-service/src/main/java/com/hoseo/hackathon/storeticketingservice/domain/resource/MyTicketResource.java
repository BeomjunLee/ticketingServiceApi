package com.hoseo.hackathon.storeticketingservice.domain.resource;

import com.hoseo.hackathon.storeticketingservice.api.ApiMemberController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.MyTicketDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class MyTicketResource extends EntityModel<MyTicketDto> {
    public MyTicketResource(MyTicketDto dto, Link... links) {
        super(dto, links);
        add(linkTo(ApiMemberController.class).slash("tickets").withSelfRel());
        add(linkTo(ApiMemberController.class).slash("tickets").withRel("취소"));
    }
}
