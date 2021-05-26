package com.hoseo.hackathon.storeticketingservice.domain;
import com.hoseo.hackathon.storeticketingservice.domain.form.MemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.status.ErrorStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.MemberRole;
import com.hoseo.hackathon.storeticketingservice.domain.status.MemberStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreStatus;
import com.hoseo.hackathon.storeticketingservice.exception.DuplicateTicketingException;
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
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    @ApiModelProperty(hidden = true)
    private Long id;
    
    @Column(unique = true)
    private String username;                                //아이디

    private String password;                                //비밀번호

    private String name;                                    //이름

    private String phoneNum;                                //전화번호

    private String email;                                   //이메일

    private int point;                                      //포인트
    
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;                            //가입 대기(VALID, INVALID) 탈퇴 (DELETE)

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<MemberRole> roles = new HashSet<>();        //권한

    private LocalDateTime createdDate;                      //가입일
    private LocalDateTime deletedDate;                      //탈퇴일

    private String refreshToken;                            //refreshToken

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)  //양방향 매핑
    private List<Ticket> ticketList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    //==연관관계 세팅==
    public void setStore(Store store) {
        this.store = store;
        store.getMemberList().add(this);
    }

    /**
     * 일반 회원 가입
     * @param memberForm 회원가입 form
     * @param encodingPassword 비밀번호 암호화
     * @return
     */
    public static Member createMember(MemberForm memberForm, String encodingPassword) {
        Member member = Member.builder()
                .username(memberForm.getUsername())
                .password(encodingPassword)
                .name(memberForm.getName())
                .phoneNum(memberForm.getPhoneNum())
                .email(memberForm.getEmail())
                .createdDate(LocalDateTime.now())
                .deletedDate(null)  //탈퇴일은 가입시 null(재가입시 null 로 바꿔야돼서)
                .memberStatus(MemberStatus.VALID)
                .build();
        member.addRole(MemberRole.USER);   //권한부여
        return member;
    }

    /**
     * 일반 회원 가입
     * @param storeAdminForm 회원가입 form
     * @param store 매장 entity
     * @param encodingPassword 비밀번호 암호화
     * @return
     */
    public static Member createStoreAdmin(StoreAdminForm storeAdminForm, Store store, String encodingPassword) {
        Member member = Member.builder()//회원
                .username(storeAdminForm.getMemberUsername())
                .password(encodingPassword)
                .name(storeAdminForm.getMemberName())
                .phoneNum(storeAdminForm.getMemberPhoneNum())
                .email(storeAdminForm.getMemberEmail())
                .createdDate(LocalDateTime.now())
                .memberStatus(MemberStatus.INVALID)
                .build();
        member.addRole(MemberRole.STORE_ADMIN); //권한부여

        member.setStore(store);
        return member;
    }

    /**
     * 번호표 검증
     */
    public void verifyTicket() {
        if(getTicketList().size() > 0)
            throw new DuplicateTicketingException("이미 번호표를 가지고 있습니다");
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
