package com.hoseo.hackathon.storeticketingservice.domain.member.controller.hateoas;

import com.hoseo.hackathon.storeticketingservice.domain.member.controller.MemberController;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.MemberDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class MemberResource extends EntityModel<MemberDto> {
    public MemberResource(MemberDto memberDTO, Link... links){
        super(memberDTO, links);
        add(linkTo(MemberController.class).slash("me").withSelfRel());
        add(linkTo(MemberController.class).slash("me").withRel("회원 수정"));
    }
}
