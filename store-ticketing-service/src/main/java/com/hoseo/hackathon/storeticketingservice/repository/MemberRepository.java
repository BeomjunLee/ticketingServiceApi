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

    @Query("select distinct m from Member m left join fetch m.ticketList where m.username = :username")
    Optional<Member> findMemberJoinTicketByUsername(@Param("username") String username);

    @Query("select m from Member m join fetch m.store where m.username = :username")
    Optional<Member> findByUsernameJoinStore(@Param("username") String username);

    @Query("select m from Member m join fetch m.store where m.id = :id")
    Optional<Member> findByMemberIdJoinStore(@Param("id") Long id);

    Optional<Member> findMemberByUsernameAndRefreshToken(String username, String refreshToken);

    //전체 회원수
    int countByMemberStatus(MemberStatus memberStatus);
    
    //아이디 중복검색
    int countByUsername(String username);

    //탈퇴한 회원들 검색
    List<Member> findAllByMemberStatus(MemberStatus memberStatus);

    @Override
    void delete(Member entity);

    //refreshToken 검색 최적화
    @Query("select m.refreshToken from Member m where m.username = :username")
    Optional<String> findRefreshToken(@Param("username") String username);

    @Query("select m from Member m join fetch m.roles where m.username = :username")
    Optional<Member> findMemberByUsernameFetch(@Param("username") String username);
}
