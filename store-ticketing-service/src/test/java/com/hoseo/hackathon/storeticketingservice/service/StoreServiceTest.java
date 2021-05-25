package com.hoseo.hackathon.storeticketingservice.service;
import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.Ticket;
import com.hoseo.hackathon.storeticketingservice.exception.DuplicateTicketingException;
import com.hoseo.hackathon.storeticketingservice.exception.NotFoundTicketException;
import com.hoseo.hackathon.storeticketingservice.exception.StoreTicketIsCloseException;
import com.hoseo.hackathon.storeticketingservice.repository.MemberRepository;
import com.hoseo.hackathon.storeticketingservice.repository.StoreRepository;
import com.hoseo.hackathon.storeticketingservice.repository.TicketRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StoreServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    StoreService storeService;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AdminService adminService;
    @Autowired EntityManager em;

//    @BeforeAll
    public static void createStore(@Autowired MemberService memberService,
                                   @Autowired AdminService adminService,
                                   @Autowired StoreService storeService) {
        Member storeAdmin = Member.builder()
                .username("storeadmin")
                .password("1234")
                .build();
        Store store = Store.builder()
                .name("식당")
                .member(storeAdmin)
                .build();
        memberService.createStoreAdmin(storeAdmin, store);//가게 관리자 + 가게 생성

        for (int i = 1; i <= 10; i++) {
            Member member = Member.builder()
                    .username("test" + i)
                    .password("1234")
                    .build();
            memberService.createMember(member);     //일반 회원 생성
        }
        adminService.permitStoreAdmin(storeAdmin.getId(), store.getId());   //사이트 관리자가 가입 승인
        storeService.openTicket(storeAdmin.getUsername()); //번호표 발급 허용
    }
    
    @Test
    public void 번호표뽑기_중복뽑기_번호표조회_CLOSE_상태뽑기불가_번호표취소() throws Exception{
        //given
        Ticket ticket = Ticket.builder()
                .peopleCount(5)
                .build();
        //when
        Store findStore = storeRepository.findByName("식당").get();
        
        storeService.createTicket(ticket, findStore.getId(), "test");   //티켓 생성
        //then
        assertEquals(1, findStore.getTotalWaitingCount());//총 대기인원 1명
        assertEquals(5, findStore.getTotalWaitingTime());//총 대기 시간 5분

        Ticket findTicket = storeService.findMyTicket("test");

        assertEquals(5, findTicket.getPeopleCount());   // 인원수
        assertEquals(1, findTicket.getWaitingNum());    //총 대기인원 1명
        assertEquals(5, findTicket.getWaitingTime());   //총 대기 시간 5분

        assertThrows(DuplicateTicketingException.class, () ->{
            storeService.createTicket(ticket, findStore.getId(), "test");   //번호표 중복 생성시 오류
        });

        storeService.closeTicket("storeadmin"); //번호표 비활성화
        assertThrows(StoreTicketIsCloseException.class, () ->{
            storeService.createTicket(ticket, findStore.getId(), "test");   //비활성화시 번호표 생성 오류
        });

        storeService.cancelTicket("test");  //번호표 취소
        assertThrows(NotFoundTicketException.class, () ->{
            storeService.findMyTicket("test");  //번호표 찾기 오류
        });
        assertEquals(0, findStore.getTotalWaitingCount());//총 대기인원 0명
        assertEquals(0, findStore.getTotalWaitingTime());//총 대기 시간 0분
    }

    @Test
    void fetchTest() {
        storeService.closeTicket("storeadmin");
    }

    @Test
    @DisplayName("번호표 뽑기")
    void createTicket() {
        Store store = storeRepository.findStoreJoinMemberByUsername("storeadmin2").get();

        Ticket ticket = Ticket.builder()
                .peopleCount(5)
                .build();
        storeService.createTicket(ticket, store.getId(), "test");
    }

    @Test
    @DisplayName("매장 조회시 해당 매장 관리자 까지 조회 (n + 1)")
    public void fetchJoin() throws Exception{
        //given
        em.clear();
        //when
        Store store = storeRepository.findStoreJoinMemberByUsername("storeadmin").get();
        //then
        System.out.println("store.getName() = " + store.getName());
        System.out.println("store.getMember().getName() = " + store.getMember().getUsername());
    }
}