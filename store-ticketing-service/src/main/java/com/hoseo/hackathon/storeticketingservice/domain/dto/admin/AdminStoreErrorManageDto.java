package com.hoseo.hackathon.storeticketingservice.domain.dto.admin;

import com.hoseo.hackathon.storeticketingservice.domain.resource.admin.AdminStoreErrorResource;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.PagedModel;

@Data
@Builder
public class AdminStoreErrorManageDto {
    private PagedModel<AdminStoreErrorResource> errorList;
}
