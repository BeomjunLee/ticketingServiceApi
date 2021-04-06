package com.hoseo.hackathon.storeticketingservice;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.Ticket;
import com.hoseo.hackathon.storeticketingservice.service.AdminService;
import com.hoseo.hackathon.storeticketingservice.service.MemberService;
import com.hoseo.hackathon.storeticketingservice.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

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

    @PostConstruct
    public void createTicket() {
        Member storeAdmin = Member.builder()
                .username("storeadmin")
                .password("1234")
                .build();
        Store store = Store.builder()
                .name("식당")
                .member(storeAdmin)
                .build();
        memberService.createStoreAdmin(storeAdmin, store);//가게 관리자 + 가게 생성
        adminService.permitStoreAdmin(storeAdmin.getId(), store.getId());   //사이트 관리자가 가입 승인
        storeService.openTicket(storeAdmin.getUsername()); //번호표 발급 허용
        Store findStore = storeService.findStore("storeadmin");

        for(int i = 1; i <= 30; i++) {
            Member member = Member.builder()
                    .username("member"+i)
                    .password("1234")
                    .build();
            Member savedMember = memberService.createMember(member); //회원 생성
            Ticket ticket = Ticket.builder().peopleCount(i).build();
            Ticket savedTicket = storeService.createTicket(ticket, findStore.getId(), savedMember.getUsername()); //번호표 발급

        }
    }

}
