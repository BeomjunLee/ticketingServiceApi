package com.hoseo.hackathon.storeticketingservice.domain.admin.dto;

import com.hoseo.hackathon.storeticketingservice.domain.admin.controller.hateoas.AdminStoreErrorResource;
import lombok.*;
import org.springframework.hateoas.PagedModel;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminStoreErrorManageDto {
    private PagedModel<AdminStoreErrorResource> errorList;
}
