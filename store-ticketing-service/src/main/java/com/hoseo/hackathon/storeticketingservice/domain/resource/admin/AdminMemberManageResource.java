package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.controller.StoreController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.admin.AdminMemberManageDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * 회원 관리 dto + hateoas link
 */
public class AdminMemberManageResource extends EntityModel<AdminMemberManageDto> {
    public AdminMemberManageResource(AdminMemberManageDto dto, Link... links) {
        super(dto, links);
        add(linkTo(StoreController.class).slash("delete-members").withRel("탈퇴후 7일지난 회원 영구삭제"));
    }
}
