package com.hoseo.hackathon.storeticketingservice.domain.ticket.controller.hateoas;

import com.hoseo.hackathon.storeticketingservice.domain.member.controller.MemberController;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.dto.MyTicketDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class MyTicketResource extends EntityModel<MyTicketDto> {
    public MyTicketResource(MyTicketDto dto, Link... links) {
        super(dto, links);
        add(linkTo(MemberController.class).slash("tickets").withSelfRel());
        add(linkTo(MemberController.class).slash("cancel-ticket").withRel("취소"));
    }
}
