package com.hoseo.hackathon.storeticketingservice.domain.status;

import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
public enum Role {
    USER("ROLE_USER"), STORE_ADMIN("ROLE_STORE_ADMIN"), ADMIN("ROLE_ADMIN");

    private String role;

    Role(String name) {
        this.role = name;
    }

    //enum객체 String비교로직
    public boolean isCorrectName(String name) {
        return name.equalsIgnoreCase(this.role);
    }

    //name을 MemberRole에서 찾아서 리턴
    public static Role getRoleByName(String name) {
        return Arrays.stream(Role.values()).filter(r -> r.isCorrectName(name)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("검색된 권한이 없습니다"));
    }
}
