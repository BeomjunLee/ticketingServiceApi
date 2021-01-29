package com.hoseo.hackathon.storeticketingservice.domain.resource;

import com.hoseo.hackathon.storeticketingservice.api.ApiMemberController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.MemberDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class MemberResource extends EntityModel<MemberDto> {
    public MemberResource(MemberDto memberDTO, Link... links){
        super(memberDTO, links);
        add(linkTo(ApiMemberController.class).slash("me").withSelfRel());
        add(linkTo(ApiMemberController.class).slash("me").slash("points").withRel("포인트 사용"));
        add(linkTo(ApiMemberController.class).slash("me/password").withRel("비밀번호 변경"));
        add(linkTo(ApiMemberController.class).slash("me").withRel("회원 수정"));
        add(linkTo(ApiMemberController.class).slash("me").withRel("회원 탈퇴"));
    }
}
