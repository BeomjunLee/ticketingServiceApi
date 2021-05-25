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


//    //테스트 데이터
//    @PostConstruct
//    public void createTicket() {
//        //매장1
//        Member storeAdmin = Member.builder()
//                .username("storeadmin")
//                .password("1234")
//                .build();
//        Store store = Store.builder()
//                .name("식당")
//                .member(storeAdmin)
//                .avgWaitingTimeByOne(10)
//                .build();
//
//
//        memberService.createStoreAdmin(storeAdmin, store);//가게 관리자 + 가게 생성
//        adminService.permitStoreAdmin(storeAdmin.getId(), store.getId());   //사이트 관리자가 가입 승인
//        storeService.openTicket(storeAdmin.getUsername()); //번호표 발급 허용
//        Store findStore = storeService.findStore("storeadmin");
//
//        //매장2
//        Member storeAdmin2 = Member.builder()
//                .username("storeadmin2")
//                .password("1234")
//                .build();
//        Store store2 = Store.builder()
//                .name("식당2")
//                .member(storeAdmin2)
//                .build();
//
//        memberService.createStoreAdmin(storeAdmin2, store2);//가게 관리자 + 가게 생성
//        adminService.permitStoreAdmin(storeAdmin2.getId(), store2.getId());   //사이트 관리자가 가입 승인
//        storeService.openTicket(storeAdmin2.getUsername()); //번호표 발급 허용
//        Store findStore2 = storeService.findStore("storeadmin2");
//
//        //매장1에 번호표 발급
//        for(int i = 1; i <= 30; i++) {
//            Member member = Member.builder()
//                    .username("member"+i)
//                    .password("1234")
//                    .name("회원"+i)
//                    .phoneNum("010-"+i)
//                    .email("email"+i+"@email.com")
//                    .createdDate(LocalDateTime.now())
//                    .point(i)
//                    .build();
//            Member savedMember = memberService.createMember(member); //회원 생성
//            Ticket ticket = Ticket.builder().peopleCount(i).build();
//            Ticket savedTicket = storeService.createTicket(ticket, findStore.getId(), savedMember.getUsername()); //번호표 발급
//
//        }
//
//        //매장2에 번호표 발급
//        Member member = Member.builder()
//                .username("test")
//                .password("1234")
//                .name("회원")
//                .phoneNum("01012341234")
//                .email("email@email.com")
//                .createdDate(LocalDateTime.now())
//                .point(100)
//                .build();
//        Member savedMember = memberService.createMember(member); //회원 생성
//        Ticket ticket = Ticket.builder().peopleCount(5).build();
////        Ticket savedTicket = storeService.createTicket(ticket, findStore2.getId(), savedMember.getUsername()); //번호표 발급
//

//    }

}
