package com.hoseo.hackathon.storeticketingservice.domain.resource.admin;

import com.hoseo.hackathon.storeticketingservice.controller.AdminController;
import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreManageDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * 매장 관리 + hateoas link
 */
public class AdminWaitingMembersAndStoreManageResource extends EntityModel<StoreManageDto> { //가게 관리 메서드에 사용
    public AdminWaitingMembersAndStoreManageResource(StoreManageDto dto, Link... links){
        super(dto, links);
        add(linkTo(AdminController.class).slash("stores").slash(dto.getStore_id()).slash("holding").withRel("보류된 번호표 리스트 관리"));
        add(linkTo(AdminController.class).slash("stores").slash(dto.getStore_id()).slash("open-status").withRel("번호표 OPEN"));
        add(linkTo(AdminController.class).slash("stores").slash(dto.getStore_id()).slash("close-status").withRel("번호표 CLOSE"));
        add(linkTo(AdminController.class).slash("stores").slash(dto.getStore_id()).slash("apply-errors").withRel("오류 접수"));
        add(linkTo(AdminController.class).slash("stores").slash(dto.getStore_id()).slash("edit").withRel("매장 정보 수정"));
    }
}
