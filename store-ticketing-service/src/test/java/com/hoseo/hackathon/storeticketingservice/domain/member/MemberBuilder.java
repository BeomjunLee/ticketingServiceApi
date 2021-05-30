package com.hoseo.hackathon.storeticketingservice.domain.member;

import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.MemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.Member;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;

public class MemberBuilder {

    public static Member memberBuild (MemberForm memberForm) {
       return Member.createMember(memberForm, memberForm.getPassword());
    }


    public static Member storeAdminBuild(StoreAdminForm storeAdminForm, Store store) {
        return Member.createStoreAdmin(storeAdminForm, store, storeAdminForm.getMemberPassword());
    }
}
