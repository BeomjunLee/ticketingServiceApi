package com.hoseo.hackathon.storeticketingservice.domain.admin.dto;

import com.hoseo.hackathon.storeticketingservice.domain.store.dto.StoreListDto;
import lombok.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * 매장 관리 dto (admin 용)
 */
public class AdminStoreManageDto {
    private PagedModel<EntityModel<StoreListDto>> storeList;
    private int totalEnrollStoreCount;
}
