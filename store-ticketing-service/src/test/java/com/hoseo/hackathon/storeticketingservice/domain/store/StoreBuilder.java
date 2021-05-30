package com.hoseo.hackathon.storeticketingservice.domain.store;

import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;

public class StoreBuilder {

    public static Store build(StoreAdminForm storeAdminForm) {
        return Store.createStore(storeAdminForm);
    }


}
