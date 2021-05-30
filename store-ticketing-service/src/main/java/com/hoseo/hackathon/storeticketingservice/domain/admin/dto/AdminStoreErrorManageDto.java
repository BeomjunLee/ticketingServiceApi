package com.hoseo.hackathon.storeticketingservice.domain.admin.dto;

import com.hoseo.hackathon.storeticketingservice.domain.admin.controller.hateoas.AdminStoreErrorResource;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.PagedModel;

@Data
@Builder
public class AdminStoreErrorManageDto {
    private PagedModel<AdminStoreErrorResource> errorList;
}
