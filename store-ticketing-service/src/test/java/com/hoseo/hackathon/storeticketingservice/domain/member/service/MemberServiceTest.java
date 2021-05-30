package com.hoseo.hackathon.storeticketingservice.domain.member.service;


import com.hoseo.hackathon.storeticketingservice.domain.member.MemberBuilder;
import com.hoseo.hackathon.storeticketingservice.domain.FormBuilder;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.MemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.UpdateMemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.UpdateStoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.Member;
import com.hoseo.hackathon.storeticketingservice.domain.member.exception.DuplicateStoreNameException;
import com.hoseo.hackathon.storeticketingservice.domain.member.exception.DuplicateUsernameException;
import com.hoseo.hackathon.storeticketingservice.domain.member.repository.MemberRepository;
import com.hoseo.hackathon.storeticketingservice.domain.store.StoreBuilder;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private MemberForm memberForm;
    private StoreAdminForm storeAdminForm;
    private UpdateMemberForm updateMemberForm;
    private UpdateStoreAdminForm updateStoreAdminForm;
    private Member member;
    private Member storeAdmin;
    private Store store;

    @BeforeEach
    public void setUp() {
        memberForm = FormBuilder.memberFormBuild();
        storeAdminForm = FormBuilder.storeAdminFormBuild();
        updateMemberForm = FormBuilder.updateMemberFormBuild();
        updateStoreAdminForm = FormBuilder.updateStoreAdminFormBuild();

        member = MemberBuilder.memberBuild(memberForm);

        store = StoreBuilder.build(storeAdminForm);

        storeAdmin = MemberBuilder.storeAdminBuild(storeAdminForm, store);
        storeAdmin.setStore(store);
    }

    @Test
    @DisplayName("일반회원 가입 성공 테스트")
    public void createMemberSuccess() throws Exception{
        //given
        when(memberRepository.countByUsername(any())).thenReturn(0);
        when(memberRepository.save(any())).thenReturn(member);

        //when
        Member savedMember = memberService.createMember(memberForm);

        //then
        assertThat(savedMember.getUsername()).isEqualTo(memberForm.getUsername());
        assertThat(savedMember.getPassword()).isEqualTo(memberForm.getPassword());
        assertThat(savedMember.getName()).isEqualTo(memberForm.getName());
        assertThat(savedMember.getEmail()).isEqualTo(memberForm.getEmail());
        assertThat(savedMember.getPhoneNum()).isEqualTo(memberForm.getPhoneNum());
    }

    @Test
    @DisplayName("일반회원 가입 실패 테스트 (아이디 중복)")
    public void createMemberFail() throws Exception{
        //given
        when(memberRepository.countByUsername(any())).thenReturn(1);

        //when, then
        assertThatThrownBy(() -> {
            memberService.createMember(memberForm);
        }).isInstanceOf(DuplicateUsernameException.class).hasMessageContaining("아이디가 중복되었습니다");
    }

    @Test
    @DisplayName("매장 관리자 회원 가입 성공 테스트")
    public void createStoreAdminSuccess() throws Exception{
        //given
        when(memberRepository.countByUsername(any())).thenReturn(0);
        when(storeRepository.countByName(any())).thenReturn(0);
        when(storeRepository.save(any())).thenReturn(store);
        when(memberRepository.save(any())).thenReturn(storeAdmin);

        //when
        Member savedMember = memberService.createStoreAdmin(storeAdminForm);

        //then
        assertThat(savedMember.getUsername()).isEqualTo(storeAdminForm.getMemberUsername());
        assertThat(savedMember.getPassword()).isEqualTo(storeAdminForm.getMemberPassword());
        assertThat(savedMember.getName()).isEqualTo(storeAdminForm.getMemberName());
        assertThat(savedMember.getEmail()).isEqualTo(storeAdminForm.getMemberEmail());
        assertThat(savedMember.getPhoneNum()).isEqualTo(storeAdminForm.getMemberPhoneNum());
    }

    @Test
    @DisplayName("매장 관리자 회원 가입 실패 테스트 (아이디 중복)")
    public void createStoreAdminFail_1() throws Exception{
        //given
        when(memberRepository.countByUsername(any())).thenReturn(1);
        //when, then
        assertThatThrownBy(() -> {
            memberService.createMember(memberForm);
        }).isInstanceOf(DuplicateUsernameException.class).hasMessageContaining("아이디가 중복되었습니다");
    }

    @Test
    @DisplayName("매장 관리자 회원 가입 실패 테스트 (매장명 중복)")
    public void createStoreAdminFail_2() throws Exception{
        //given
        when(storeRepository.countByName(any())).thenReturn(1);
        //when, then
        assertThatThrownBy(() -> {
            memberService.createStoreAdmin(storeAdminForm);
        }).isInstanceOf(DuplicateStoreNameException.class).hasMessageContaining("매장명이 중복되었습니다");
    }

    @Test
    @DisplayName("회원 정보 조회")
    public void findMember() throws Exception{
        //given
        when(memberRepository.findByUsername(any())).thenReturn(Optional.of(member));

        //when
        Member findMember = memberService.findMemberByUsername(any());

        //then
        assertThat(findMember).usingRecursiveComparison().isEqualTo(member);
    }

    @Test
    @DisplayName("회원 정보 조회 실패")
    public void findMemberFail() throws Exception{
        //given
        when(memberRepository.findByUsername(any())).thenReturn(Optional.empty());
        //when, then
        assertThatThrownBy(() -> {
            memberService.findMemberByUsername(any());
        }).isInstanceOf(UsernameNotFoundException.class).hasMessageContaining("해당되는 유저를 찾을수 없습니다");
    }

    @Test
    @DisplayName("회원 수정")
    public void updateMember() throws Exception{
        //given
        when(memberRepository.findByUsername(any())).thenReturn(Optional.of(member));

        //when
        Member updatedMember = memberService.updateMember(any(), updateMemberForm);

        //then
        assertThat(updatedMember.getName()).isEqualTo(updateMemberForm.getName());
        assertThat(updatedMember.getEmail()).isEqualTo(updateMemberForm.getEmail());
        assertThat(updatedMember.getPhoneNum()).isEqualTo(updateMemberForm.getPhoneNum());
    }

    @Test
    @DisplayName("매장 관리자 수정")
    public void updateStoreAdmin() throws Exception{
        //given
        when(memberRepository.findMemberJoinStoreByUsername(any())).thenReturn(Optional.of(storeAdmin));

        //when
        Member updatedMember = memberService.updateStoreAdmin(any(), updateStoreAdminForm);

        //then
        assertThat(updatedMember.getName()).isEqualTo(updateStoreAdminForm.getMember_name());
        assertThat(updatedMember.getEmail()).isEqualTo(updateStoreAdminForm.getMember_email());
        assertThat(updatedMember.getPhoneNum()).isEqualTo(updateStoreAdminForm.getMember_phoneNum());
        assertThat(updatedMember.getStore().getAddress()).isEqualTo(updateStoreAdminForm.getStore_address());
        assertThat(updatedMember.getStore().getPhoneNum()).isEqualTo(updateStoreAdminForm.getStore_phoneNum());
    }
}
