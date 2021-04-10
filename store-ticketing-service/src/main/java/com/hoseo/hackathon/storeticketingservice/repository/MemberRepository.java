package com.hoseo.hackathon.storeticketingservice.repository;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.status.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findMemberByUsernameAndRefreshToken(String username, String refreshToken);

    //전체 회원수
    int countByStatus(MemberStatus status);
    
    //아이디 중복검색
    int countByUsername(String username);

    //탈퇴한 회원들 검색
    List<Member> findAllByStatus(MemberStatus status);

    @Override
    void delete(Member entity);

    //refreshToken 검색 최적화
    @Query("select m.refreshToken from Member m where m.username = :username")
    Optional<String> findRefreshToken(@Param("username") String username);

    @Query("select m from Member m join fetch m.roles where m.username = :username")
    Optional<Member> findMemberByUsernameFetch(@Param("username") String username);
}
