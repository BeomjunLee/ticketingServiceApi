package com.hoseo.hackathon.storeticketingservice.optimization;

import com.hoseo.hackathon.storeticketingservice.domain.admin.service.AdminService;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.Member;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.domain.store.service.StoreService;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.MemberListDto;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.MemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.dto.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.enums.MemberStatus;
import com.hoseo.hackathon.storeticketingservice.domain.member.service.MemberService;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.enums.StoreStatus;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.service.TicketService;
import com.hoseo.hackathon.storeticketingservice.domain.member.repository.condition.MemberSearchCondition;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AdminServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    AdminService adminService;
    @Autowired
    StoreService storeService;
    @Autowired
    TicketService ticketService;

    @BeforeAll
    public static void createStore(@Autowired MemberService memberService,
                                   @Autowired AdminService adminService,
                                   @Autowired StoreService storeService,
                                   @Autowired TicketService ticketService) {
        //매장1
        StoreAdminForm storeAdminForm = StoreAdminForm.builder()
                .memberUsername("storeadmin")
                .memberPassword("1234")
                .storeName("식당")
                .build();

        Member storeAdmin = memberService.createStoreAdmin(storeAdminForm);//매장 관리자 + 매장 생성
        adminService.permitStoreAdmin(storeAdmin.getId());   //사이트 관리자가 가입 승인
        storeService.openTicket(storeAdmin.getUsername()); //번호표 발급 허용
        Store findStore = storeService.findStore("storeadmin");

        //매장2
        StoreAdminForm storeAdminForm2 = StoreAdminForm.builder()
                .memberUsername("storeadmin2")
                .memberPassword("1234")
                .storeName("식당2")
                .build();

        Member storeAdmin2 = memberService.createStoreAdmin(storeAdminForm2);//가게 관리자 + 가게 생성
        adminService.permitStoreAdmin(storeAdmin2.getId());   //사이트 관리자가 가입 승인
        storeService.openTicket(storeAdmin2.getUsername()); //번호표 발급 허용

        //매장1에 번호표 발급
        for (int i = 1; i <= 30; i++) {
            MemberForm member = MemberForm.builder()
                    .username("member" + i)
                    .password("1234")
                    .name("회원" + i)
                    .phoneNum("010-" + i)
                    .email("email" + i + "@email.com")
                    .build();
            Member savedMember = memberService.createMember(member); //회원 생성
            TicketForm ticket = TicketForm.builder().peopleCount(i).build();
            ticketService.createTicket(ticket, findStore.getId(), savedMember.getUsername()); //번호표 발급

        }
    }

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
        Assertions.assertThat(content.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("매장 관리자 가입 취소")
    void cancelPermitStoreAdmin() {
        adminService.cancelPermitStoreAdmin(3L);

        Member member = adminService.findMember(3L);
        Store store = adminService.findStore(4L);

        Assertions.assertThat(member.getMemberStatus()).isEqualTo(MemberStatus.INVALID);
        Assertions.assertThat(store.getStoreStatus()).isEqualTo(StoreStatus.INVALID);
    }

}