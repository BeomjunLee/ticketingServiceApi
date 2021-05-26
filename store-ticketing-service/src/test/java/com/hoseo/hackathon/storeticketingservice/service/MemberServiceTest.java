package com.hoseo.hackathon.storeticketingservice.service;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.form.MemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.UpdateMemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.UpdateStoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.exception.DuplicateStoreNameException;
import com.hoseo.hackathon.storeticketingservice.exception.DuplicateUsernameException;
import com.hoseo.hackathon.storeticketingservice.repository.MemberRepository;
import com.hoseo.hackathon.storeticketingservice.repository.StoreQueryRepository;
import com.hoseo.hackathon.storeticketingservice.repository.StoreRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    EntityManager em;
    @Autowired
    StoreQueryRepository storeQueryRepository;

    @Test
    public void 로그인_체크() {
        MemberForm member = MemberForm.builder()
                .username("test")
                .password("1234")
                .build();
        memberService.createMember(member);
    }

    @Test
    public void 비밀번호_변경() {
        MemberForm member = MemberForm.builder()
                .username("test")
                .password("1234")
                .build();
        Member savedMember = memberService.createMember(member);
        memberService.changePassword("test","1234", "12345");
        assertEquals(true, passwordEncoder.matches("12345", memberRepository.findById(savedMember.getId()).get().getPassword()));
    }

    @Test
    public void 회원가입() {
        for (int i = 0; i < 10; i++) {
            MemberForm member = MemberForm.builder()
                    .username("test"+i)
                    .password("1234")
                    .build();
            Member savedMember = memberService.createMember(member);
        }
        em.flush();
        em.clear();

        List<Member> memberList = memberRepository.findAll();
        Assertions.assertThat(memberList.size()).isEqualTo(10);
    }

    @Test
    public void 중복_회원가입() {
        MemberForm member1 = MemberForm.builder()
                .username("test")
                .password("1234")
                .build();

        MemberForm member2 = MemberForm.builder()
                .username("test")
                .password("1234")
                .build();
        memberService.createMember(member1);
        assertThrows(DuplicateUsernameException.class, () -> {
            memberService.createMember(member2);
        });
    }

    @Test
    public void 가게관리자_회원가입_정보보기() {
        StoreAdminForm storeAdminForm = StoreAdminForm.builder()
                .memberUsername("test")
                .memberPassword("1234")
                .storeName("식당")
                .build();
        memberService.createStoreAdmin(storeAdminForm);
        Member member = memberRepository.findByUsername("test").get();
        assertEquals(storeAdminForm.getMemberUsername(), member.getUsername());

        Store store = storeRepository.findByName("식당").get();
        assertEquals(storeAdminForm.getStoreName(), store.getName());

        assertEquals("승인 대기", store.getStoreStatus().getStatus());
    }

    @Test
    public void 가게관리자_중복회원가입() {
        StoreAdminForm storeAdminForm1 = StoreAdminForm.builder()
                .memberUsername("test")
                .memberPassword("1234")
                .storeName("식당")
                .build();

        StoreAdminForm storeAdminForm2 = StoreAdminForm.builder()
                .memberUsername("test2")
                .memberPassword("1234")
                .storeName("식당2")
                .build();
        memberService.createStoreAdmin(storeAdminForm1);

        assertThrows(DuplicateStoreNameException.class, () ->{
            memberService.createStoreAdmin(storeAdminForm2);
        });
    }
    
    @Test
    public void 회원_수정() throws Exception{
        //given
        MemberForm member = MemberForm.builder()
                .username("test")
                .password("1234")
                .name("테스트")
                .phoneNum("1")
                .email("a@a")
                .build();
        //when
        Member savedMember = memberService.createMember(member);

        UpdateMemberForm form = UpdateMemberForm.builder()
                .name("테스트1")
                .phoneNum("2")
                .email("b@b")
                .build();

        memberService.updateMember(member.getUsername(), form);

        Member findMember = memberRepository.findById(savedMember.getId()).get();
        //then
        assertEquals(form.getName(), findMember.getName());
        assertEquals(form.getPhoneNum(), findMember.getPhoneNum());
        assertEquals(form.getEmail(), findMember.getEmail());
    }
    
    @Test
    public void 가게관리자_수정() throws Exception{
        //given
        StoreAdminForm storeAdminForm = StoreAdminForm.builder()
                .memberUsername("test")
                .memberPassword("1234")
                .memberName("테스트")
                .memberPhoneNum("1")
                .memberEmail("a@a")
                .storeName("식당")
                .storePhoneNum("1")
                .storeAddress("주소")
                .build();
        memberService.createStoreAdmin(storeAdminForm);
        //when
        UpdateStoreAdminForm form = UpdateStoreAdminForm.builder()
                .member_name("테스트1")
                .member_phoneNum("2")
                .member_email("b@b")
                .store_phoneNum("2")
                .store_address("주소1")
                .build();
        memberService.updateStoreAdmin(storeAdminForm.getMemberUsername(), form);

        Member findMember = memberRepository.findByUsername(storeAdminForm.getMemberUsername()).get();
        Store findStore = storeQueryRepository.findMemberJoinStoreByUsername(findMember.getUsername()).get();
        //then


        assertEquals(form.getMember_name(), findMember.getName());
        assertEquals(form.getMember_phoneNum(), findMember.getPhoneNum());
        assertEquals(form.getMember_email(), findMember.getEmail());
        assertEquals(form.getStore_phoneNum(), findStore.getPhoneNum());
        assertEquals(form.getStore_address(), findStore.getAddress());
    }



}