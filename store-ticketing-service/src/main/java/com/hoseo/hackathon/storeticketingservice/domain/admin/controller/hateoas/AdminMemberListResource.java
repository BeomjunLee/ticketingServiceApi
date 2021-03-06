package com.hoseo.hackathon.storeticketingservice.domain.admin.controller.hateoas;

import com.hoseo.hackathon.storeticketingservice.domain.admin.controller.AdminController;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.MemberListDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminMemberListResource extends EntityModel<MemberListDto> {
    public AdminMemberListResource(MemberListDto dto, Link... links) {
        super(dto, links);
        add(linkTo(AdminController.class).slash("members").slash(dto.getMember_id()).withRel("회원 탈퇴"));
        add(linkTo(AdminController.class).slash("members").slash(dto.getMember_id()).withRel("회원 수정"));
        if(dto.getTicket_id() != null) {
            add(linkTo(AdminController.class).slash("tickets").slash(dto.getTicket_id()).slash("cancel-ticket").withRel("티켓 취소"));
        }
    }
}
