package com.hoseo.hackathon.storeticketingservice.domain.dto.admin;

import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * 매장 관리 dto (admin 용)
 */
public class AdminStoreManageDto {
    private PagedModel<EntityModel<StoreListDto>> storeList;
    private int totalEnrollStoreCount;
}
