package com.hoseo.hackathon.storeticketingservice.service;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.form.UpdateMemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.UpdateStoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.exception.DuplicateStoreNameException;
import com.hoseo.hackathon.storeticketingservice.exception.DuplicateUsernameException;
import com.hoseo.hackathon.storeticketingservice.repository.MemberRepository;
import com.hoseo.hackathon.storeticketingservice.repository.StoreRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    StoreRepository storeRepository;

    @Test
    public void 로그인_체크() {
        Member member = Member.builder()
                .username("test")
                .password("1234")
                .build();
        memberService.createMember(member);
    }

    @Test
    public void 비밀번호_변경() {
        Member member = Member.builder()
                .username("test")
                .password("1234")
                .build();
        Member savedMember = memberService.createMember(member);
        memberService.changePassword("test","1234", "12345");
        assertEquals(true, passwordEncoder.matches("12345", memberRepository.findById(savedMember.getId()).get().getPassword()));
    }

    @Test
    public void 회원가입() {
        Member member = Member.builder()
                .username("test")
                .password("1234")
                .build();
        Member savedMember = memberService.createMember(member);
        assertEquals(member, memberRepository.findById(savedMember.getId()).get());
    }

    @Test
    public void 중복_회원가입() {
        Member member1 = Member.builder()
                .username("test")
                .password("1234")
                .build();

        Member member2 = Member.builder()
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
        Member member = Member.builder()
                .username("test")
                .password("1234")
                .build();
        Store store = Store.builder()
                .name("식당")
                .member(member)
                .build();
        memberService.createStoreAdmin(member, store);
        assertEquals(member, memberRepository.findById(member.getId()).get());

        Store findStore = storeRepository.findById(store.getId()).get();
        assertEquals(store, findStore);

        assertEquals("승인 대기", store.getStoreStatus().getStatus());
    }

    @Test
    public void 가게관리자_중복회원가입() {
        Member member1 = Member.builder()
                .username("test")
                .password("1234")
                .build();
        Store store1 = Store.builder()
                .name("식당")
                .member(member1)
                .build();

        Member member2 = Member.builder()
                .username("test2")
                .password("1234")
                .build();
        Store store2 = Store.builder()
                .name("식당")
                .member(member2)
                .build();
        memberService.createStoreAdmin(member1, store1);

        assertThrows(DuplicateStoreNameException.class, () ->{
            memberService.createStoreAdmin(member2, store2);
        });
    }
    
    @Test
    public void 회원_수정() throws Exception{
        //given
        Member member = Member.builder()
                .username("test")
                .password("1234")
                .name("테스트")
                .phoneNum("1")
                .email("a@a")
                .build();
        //when
        memberService.createMember(member);

        UpdateMemberForm form = UpdateMemberForm.builder()
                .name("테스트1")
                .phoneNum("2")
                .email("b@b")
                .build();

        memberService.updateMember(member.getUsername(), form);

        Member findMember = memberRepository.findById(member.getId()).get();
        //then
        assertEquals(form.getName(), findMember.getName());
        assertEquals(form.getPhoneNum(), findMember.getPhoneNum());
        assertEquals(form.getEmail(), findMember.getEmail());
    }
    
    @Test
    public void 가게관리자_수정() throws Exception{
        //given
        Member member = Member.builder()
                .username("test")
                .password("1234")
                .name("테스트")
                .phoneNum("1")
                .email("a@a")
                .build();
        Store store = Store.builder()
                .name("식당")
                .phoneNum("1")
                .address("주소")
                .member(member)
                .build();
        memberService.createStoreAdmin(member, store);
        //when
        UpdateStoreAdminForm form = UpdateStoreAdminForm.builder()
                .member_name("테스트1")
                .member_phoneNum("2")
                .member_email("b@b")
                .store_phoneNum("2")
                .store_address("주소1")
                .build();
        memberService.updateStoreAdmin(member.getUsername(), form);

        Member findMember = memberRepository.findById(member.getId()).get();
        Store findStore = storeRepository.findByMember_Id(member.getId()).get();
        //then


        assertEquals(form.getMember_name(), findMember.getName());
        assertEquals(form.getMember_phoneNum(), findMember.getPhoneNum());
        assertEquals(form.getMember_email(), findMember.getEmail());
        assertEquals(form.getStore_phoneNum(), findStore.getPhoneNum());
        assertEquals(form.getStore_address(), findStore.getAddress());
    }

}