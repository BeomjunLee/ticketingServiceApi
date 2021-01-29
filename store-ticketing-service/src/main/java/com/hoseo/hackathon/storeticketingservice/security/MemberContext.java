package com.hoseo.hackathon.storeticketingservice.security;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.status.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class MemberContext extends User {

    //기본 생성자 숨기고
    private MemberContext(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    /**
     *
     */
    public MemberContext(String username, String password, String role) {
        super(username, password, authorities(role));
    }

    /**
     * [User에 username, password, authorities 넣기]
     */
    public static MemberContext setMemberContextFromMember(Member member) {
        return new MemberContext(member.getUsername(), member.getPassword(), authorities(member.getRole()));
    }

    /**
     *  [MemberRole의 role을 GrantedAuthority로 변환]
     */
    private static Collection<? extends GrantedAuthority> authorities(Role roles) {
        return Arrays.asList(roles).stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toList());
    }

    /**
     * String role을 MemberRole에서 찾아서 GrantedAuthority로
     */
    private static Collection<? extends GrantedAuthority> authorities(String role) {
        return authorities(Role.getRoleByName(role));
    }
}
