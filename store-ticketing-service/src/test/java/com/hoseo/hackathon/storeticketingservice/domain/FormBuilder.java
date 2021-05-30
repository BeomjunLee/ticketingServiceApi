package com.hoseo.hackathon.storeticketingservice.domain;

import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.MemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.UpdateMemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.UpdateStoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.dto.form.TicketForm;

public class FormBuilder {

    public static MemberForm memberFormBuild() {
        return MemberForm.builder()
                .username("username")
                .password("password")
                .name("name")
                .email("email@naver.com")
                .phoneNum("01000000000")
                .build();
    }

    public static StoreAdminForm storeAdminFormBuild() {
        return StoreAdminForm.builder()
                .memberUsername("username")
                .memberPassword("password")
                .memberName("name")
                .memberEmail("email@naver.com")
                .memberPhoneNum("01000000000")
                .storeName("store name")
                .storeAddress("address")
                .storePhoneNum("0310000000")
                .storeLatitude("1000")
                .storeLongitude("2000")
                .storeCompanyNumber("1234")
                .build();
    }

    public static UpdateMemberForm updateMemberFormBuild() {
        return UpdateMemberForm.builder()
                .name("name2")
                .email("email2@naver.com")
                .phoneNum("01011111111")
                .build();
    }

    public static UpdateStoreAdminForm updateStoreAdminFormBuild() {
        return UpdateStoreAdminForm.builder()
                .member_name("name2")
                .member_email("email2@naver.com")
                .member_phoneNum("01011111111")
                .store_address("address2")
                .store_phoneNum("0311111111")
                .build();
    }

    public static TicketForm ticketFormBuild() {
        return TicketForm.builder()
                .peopleCount(5)
                .build();
    }

}
