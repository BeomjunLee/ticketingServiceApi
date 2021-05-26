package com.hoseo.hackathon.storeticketingservice;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.form.MemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.service.AdminService;
import com.hoseo.hackathon.storeticketingservice.service.MemberService;
import com.hoseo.hackathon.storeticketingservice.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@SpringBootApplication
public class StoreTicketingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreTicketingServiceApplication.class, args);

    }
    @Autowired
    MemberService memberService;
    @Autowired
    AdminService adminService;
    @Autowired
    StoreService storeService;


    //테스트 데이터
    @PostConstruct
    public void createTicket() {
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
        for(int i = 1; i <= 30; i++) {
            MemberForm member = MemberForm.builder()
                    .username("member"+i)
                    .password("1234")
                    .name("회원"+i)
                    .phoneNum("010-"+i)
                    .email("email"+i+"@email.com")
                    .build();
            Member savedMember = memberService.createMember(member); //회원 생성
            TicketForm ticket = TicketForm.builder().peopleCount(i).build();
            Ticket savedTicket = storeService.createTicket(ticket, findStore.getId(), savedMember.getUsername()); //번호표 발급

        }

        //매장2에 번호표 발급
        MemberForm member = MemberForm.builder()
                .username("test")
                .password("1234")
                .name("회원")
                .phoneNum("01012341234")
                .email("email@email.com")
                .build();
        Member savedMember = memberService.createMember(member); //회원 생성
        Ticket ticket = Ticket.builder().peopleCount(5).build();
//        Ticket savedTicket = storeService.createTicket(ticket, findStore2.getId(), savedMember.getUsername()); //번호표 발급
    }

}
