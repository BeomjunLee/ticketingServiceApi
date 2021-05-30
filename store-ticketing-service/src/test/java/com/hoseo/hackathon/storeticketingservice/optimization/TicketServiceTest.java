package com.hoseo.hackathon.storeticketingservice.optimization;
import com.hoseo.hackathon.storeticketingservice.domain.admin.service.AdminService;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.Member;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.domain.store.service.StoreService;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.MemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.dto.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.service.MemberService;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.enums.TicketStatus;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.exception.DuplicateTicketingException;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.service.TicketService;
import com.hoseo.hackathon.storeticketingservice.global.common.exception.NotFoundTicketException;
import com.hoseo.hackathon.storeticketingservice.domain.store.exception.StoreTicketIsCloseException;
import com.hoseo.hackathon.storeticketingservice.domain.member.repository.MemberRepository;
import com.hoseo.hackathon.storeticketingservice.domain.store.repository.StoreQueryRepository;
import com.hoseo.hackathon.storeticketingservice.domain.store.repository.StoreRepository;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.repository.TicketRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TicketServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    TicketService ticketService;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    StoreService storeService;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AdminService adminService;
    @Autowired EntityManager em;
    @Autowired
    StoreQueryRepository storeQueryRepository;

    @BeforeAll
    public static void createStore(@Autowired MemberService memberService,
                                   @Autowired AdminService adminService,
                                   @Autowired StoreService storeService) {
        StoreAdminForm storeAdminForm = StoreAdminForm.builder()
                .memberUsername("storeadmin")
                .memberPassword("1234")
                .storeName("식당")
                .build();
        memberService.createStoreAdmin(storeAdminForm);//매장 관리자 + 매장 생성

        StoreAdminForm storeAdminForm2 = StoreAdminForm.builder()
                .memberUsername("storeadmin2")
                .memberPassword("1234")
                .storeName("식당2")
                .build();
        memberService.createStoreAdmin(storeAdminForm2);//매장 관리자 + 매장 생성

        for (int i = 1; i <= 10; i++) {
            MemberForm member = MemberForm.builder()
                    .username("test" + i)
                    .password("1234")
                    .build();
            memberService.createMember(member);     //일반 회원 생성
        }

        Member member = memberService.findMemberJoinStoreByUsername("storeadmin");
        Member member2 = memberService.findMemberJoinStoreByUsername("storeadmin2");
        adminService.permitStoreAdmin(member.getId());   //사이트 관리자가 가입 승인
        storeService.openTicket(member.getUsername()); //번호표 발급 허용
        adminService.permitStoreAdmin(member2.getId());   //사이트 관리자가 가입 승인
        storeService.openTicket(member2.getUsername()); //번호표 발급 허용
    }

    @Test
    public void 번호표뽑기_중복뽑기_번호표조회_CLOSE_상태뽑기불가_번호표취소() throws Exception{
        //given
        TicketForm ticketForm = TicketForm.builder()
                .peopleCount(5)
                .build();
        //when
        Store findStore = storeRepository.findByName("식당").get();

        ticketService.createTicket(ticketForm, findStore.getId(), "test1");   //티켓 생성
        //then
        assertEquals(1, findStore.getTotalWaitingCount());//총 대기인원 1명
        assertEquals(5, findStore.getTotalWaitingTime());//총 대기 시간 5분

        Ticket findTicket = ticketService.findMyTicket("test1");

        assertEquals(5, findTicket.getPeopleCount());   // 인원수
        assertEquals(1, findTicket.getWaitingNum());    //총 대기인원 1명
        assertEquals(5, findTicket.getWaitingTime());   //총 대기 시간 5분

        assertThrows(DuplicateTicketingException.class, () ->{
            ticketService.createTicket(ticketForm, findStore.getId(), "test1");   //번호표 중복 생성시 오류
        });

        storeService.closeTicket("storeadmin"); //번호표 비활성화
        assertThrows(StoreTicketIsCloseException.class, () ->{
            ticketService.createTicket(ticketForm, findStore.getId(), "test1");   //비활성화시 번호표 생성 오류
        });

        ticketService.cancelTicketByMember("test1");  //번호표 취소
        assertThrows(NotFoundTicketException.class, () ->{
            ticketService.findMyTicket("test1");  //번호표 찾기 오류
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
    public void createTicket() {
        em.clear();
        Store store = storeQueryRepository.findMemberJoinStoreByUsername("storeadmin").get();

        for (int i = 1; i <= 10; i++) {
            TicketForm ticketForm = TicketForm.builder()
                    .peopleCount(5)
                    .build();
            ticketService.createTicket(ticketForm, store.getId(), "test"+i);
        }
        Store findStore = storeRepository.findById(store.getId()).get();
        assertThat(findStore.getTotalWaitingCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("매장 조회시 해당 매장 관리자 까지 조회 (n + 1)")
    public void fetchJoin() throws Exception{
        //given
        em.clear();
        //when
        Store store = storeQueryRepository.findMemberJoinStoreByUsername("storeadmin").get();
        //then
        System.out.println("store.getName() = " + store.getName());
        System.out.println("store.getMember().getName() = " + store.getMemberList().get(0).getUsername());
    }

    @Test
    @DisplayName("회원 엔티티로 번호표 검색")
    public void searchTicketFromMember() {
        //given
        em.clear();
        Store store = storeQueryRepository.findMemberJoinStoreByUsername("storeadmin").get();

        for (int i = 1; i <= 10; i++) {
            TicketForm ticketForm = TicketForm.builder()
                    .peopleCount(5)
                    .build();
            ticketService.createTicket(ticketForm, store.getId(), "test"+i);
        }
        //when
        Member findMember = memberService.findByMemberJoinTicketByUsername("test1");
        //then
        assertThat(findMember.getUsername()).isEqualTo("test1");
        assertThat(findMember.getTicketList().get(0).getPeopleCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("번호표 취소 이력이 있는 회원들에 대한 번호표 중복 테스트")
    public void ticketException() {
        //given
        em.clear();
        Store store = storeQueryRepository.findMemberJoinStoreByUsername("storeadmin").get();

        TicketForm ticketForm = TicketForm.builder()
                .peopleCount(5)
                .build();
        ticketService.createTicket(ticketForm, store.getId(), "test1");   // 번호표 뽑기
        ticketService.createTicket(ticketForm, store.getId(), "test2");   // 번호표 뽑기
        ticketService.cancelTicketByMember("test1");
        //when
        ticketService.createTicket(ticketForm, store.getId(), "test1");   // 번호표 뽑기
        Member findMember = memberService.findByMemberJoinTicketByUsername("test1");
        //then
        assertThat(findMember.getUsername()).isEqualTo("test1");
        assertThat(findMember.getTicketList().get(0).getPeopleCount()).isEqualTo(5);
        assertThat(findMember.getTicketList().size()).isEqualTo(2);

        assertThatThrownBy(() -> {
            ticketService.createTicket(ticketForm, store.getId(), "test1");   // 번호표 중복 뽑기
        }).isInstanceOf(DuplicateTicketingException.class)
                .hasMessageContaining("이미 번호표를 가지고 있습니다");
    }

    @Test
    @DisplayName("매장 관리자가 회원 번호표 취소 성능 테스트")
    public void cancelTicketByStoreAdmin() {
        //given
        em.clear();
        Store store = storeQueryRepository.findMemberJoinStoreByUsername("storeadmin").get();
        Store store2 = storeQueryRepository.findMemberJoinStoreByUsername("storeadmin2").get();

        TicketForm ticketForm = TicketForm.builder()
                .peopleCount(5)
                .build();
        ticketService.createTicket(ticketForm, store2.getId(), "test2");// 번호표 뽑기
        //when
        Ticket ticket = ticketService.createTicket(ticketForm, store.getId(), "test1");// 번호표 뽑기
        Long ticketId = ticket.getId();
        em.flush();
        em.clear();
        ticketService.cancelTicket("storeadmin", ticketId);
        em.flush(); em.clear();
        Ticket findTicket = ticketRepository.findById(ticketId).get();

        //then
        assertThat(findTicket.getStatus()).isEqualTo(TicketStatus.CANCEL);
    }

    @Test
    @DisplayName("사이트 관리자가 회원 번호표 취소 성능 테스트")
    @Rollback(value = false)
    public void cancelTicketByAdmin() {
        //given
        em.clear();
        Store store = storeQueryRepository.findMemberJoinStoreByUsername("storeadmin").get();
        Store store2 = storeQueryRepository.findMemberJoinStoreByUsername("storeadmin2").get();

        TicketForm ticketForm = TicketForm.builder()
                .peopleCount(5)
                .build();
        ticketService.createTicket(ticketForm, store2.getId(), "test2");// 번호표 뽑기
        //when
        Ticket ticket = ticketService.createTicket(ticketForm, store.getId(), "test1");// 번호표 뽑기
        Long ticketId = ticket.getId();
        em.flush();
        em.clear();
        adminService.cancelTicket(ticketId);
        em.flush(); em.clear();
        Ticket findTicket = ticketRepository.findById(ticketId).get();

        //then
        assertThat(findTicket.getStatus()).isEqualTo(TicketStatus.CANCEL);
    }
}