package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.api.ApiStoreController;
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
        add(linkTo(ApiStoreController.class).slash("members").withSelfRel());
        add(linkTo(ApiStoreController.class).slash("delete-members").withRel("탈퇴후 7일지난 회원 영구삭제"));
        add(linkTo(ApiStoreController.class).slash("--").withRel("검색"));
        //TODO 검색 링크추가
    }
}
