package com.hoseo.hackathon.storeticketingservice.service;

import com.hoseo.hackathon.storeticketingservice.domain.dto.MemberListDto;
import com.hoseo.hackathon.storeticketingservice.domain.status.MemberStatus;
import com.hoseo.hackathon.storeticketingservice.repository.condition.MemberSearchCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class AdminServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    AdminService adminService;
    @Autowired
    StoreService storeService;

    @Test
    @DisplayName("전체회원 목록 확인")
    void findMembers() {
        Pageable pageable = PageRequest.of(0, 10);
        MemberSearchCondition condition = new MemberSearchCondition();
        Page<MemberListDto> members = adminService.findMembers(pageable, MemberStatus.VALID, condition);
        List<MemberListDto> content = members.getContent();
        for (MemberListDto dto : content) {
            System.out.println("dto = " + dto);
        }
    }

}