package com.hoseo.hackathon.storeticketingservice.domain.member.entity;
import com.hoseo.hackathon.storeticketingservice.domain.BaseEntity;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.enums.MemberStatus;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.MemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.StoreAdminForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "member_id")
    @ApiModelProperty(hidden = true)
    private Long id;
    
    @Column(unique = true)
    private String username;
    private String password;
    private String name;
    private String phoneNum;
    private String email;
    private int point;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<MemberRole> roles = new HashSet<>();

    private LocalDateTime deletedDate;

    private String refreshToken;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<Ticket> ticketList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Builder
    public Member(String username, String password, String name, String phoneNum, String email, MemberStatus memberStatus) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNum = phoneNum;
        this.email = email;
        this.memberStatus = memberStatus;
    }

    /**
     * 일반 회원 가입
     * @param memberForm 회원가입 form
     * @param encodingPassword 비밀번호 암호화
     * @return Member Entity
     */
    public static Member createMember(MemberForm memberForm, String encodingPassword) {
        Member member = Member.builder()
                .username(memberForm.getUsername())
                .password(encodingPassword)
                .name(memberForm.getName())
                .phoneNum(memberForm.getPhoneNum())
                .email(memberForm.getEmail())
                .memberStatus(MemberStatus.VALID)
                .build();
        member.addRole(MemberRole.USER);   //권한부여
        return member;
    }

    /**
     * 매장 관리자 가입
     * @param storeAdminForm 회원가입 form
     * @param store 매장 entity
     * @param encodingPassword 비밀번호 암호화
     * @return Member Entity
     */
    public static Member createStoreAdmin(StoreAdminForm storeAdminForm, Store store, String encodingPassword) {
        Member member = Member.builder()//회원
                .username(storeAdminForm.getMemberUsername())
                .password(encodingPassword)
                .name(storeAdminForm.getMemberName())
                .phoneNum(storeAdminForm.getMemberPhoneNum())
                .email(storeAdminForm.getMemberEmail())
                .memberStatus(MemberStatus.INVALID)
                .build();
        member.addRole(MemberRole.STORE_ADMIN); //권한부여

        member.setStore(store);
        return member;
    }


    //==연관관계 세팅==
    public void setStore(Store store) {
        this.store = store;
        store.getMemberList().add(this);
    }



    //refreshToken 갱신
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //회원 수정
    public void changeMember(String name, String phoneNum, String email) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.email = email;
    }

    //회원 수정(관리자용)
    public void changeMemberByAdmin(String username, String name, String phoneNum, String email, int point) {
        this.username = username;
        this.name = name;
        this.phoneNum = phoneNum;
        this.email = email;
        this.point = point;
    }

    //비밀번호 암호화위해 setter
    public void encodingPassword(String password) {
        this.password = password;
    }

    //==비지니스 로직
    public void removeRole(MemberRole role) { //권한 삭제
        this.roles.remove(role);
    }

    public void addRole(MemberRole... role) { //권한 추가
        this.roles = Set.of(role);
    }

    public void changeMemberStatus(MemberStatus memberStatus) {   //가입상태 변경
        this.memberStatus = memberStatus;
    }
}
