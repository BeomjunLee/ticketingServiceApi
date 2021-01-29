package com.hoseo.hackathon.storeticketingservice.config;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.status.Role;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreTicketStatus;
import com.hoseo.hackathon.storeticketingservice.repository.MemberRepository;
import com.hoseo.hackathon.storeticketingservice.repository.StoreRepository;
import com.hoseo.hackathon.storeticketingservice.service.MemberService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
public class AppConfig {
    //한글 인코딩
    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        //여러가지 암호화 방법들을 알아서 매칭
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

//   // 어플리케이션 시작시 test 계정 생성
//    @Bean
//    public ApplicationRunner applicationRunner() {
//        return new ApplicationRunner() {
//            @Autowired
//            MemberService memberService;
//            @Autowired
//            StoreRepository storeRepository;
//
//            @Autowired
//            MemberRepository memberRepository;
//            @Override
//            public void run(ApplicationArguments args) throws Exception {
//                Member member = Member.builder()
//                        .username("admin2")
//                        .password("1234")
//                        .role(Role.ADMIN)
//                        .build();
//
//                Member member2 = Member.builder()
//                        .username("test1")
//                        .name("회원1")
//                        .password("1234")
//                        .role(Role.USER)
//                        .build();
//
//                Member member3 = Member.builder()
//                        .username("test2")
//                        .password("1234")
//                        .role(Role.USER)
//                        .build();
//
//                Member member4 = Member.builder()
//                        .username("test3")
//                        .password("1234")
//                        .role(Role.USER)
//                        .build();
//
//                Member member5 = Member.builder()
//                        .username("test4")
//                        .password("1234")
//                        .role(Role.USER)
//                        .build();
//                Member member6 = Member.builder()
//                        .username("test5")
//                        .password("1234")
//                        .role(Role.USER)
//                        .build();
//
//                Store store = Store.builder()
//                        .name("식당1")
//                        .storeTicketStatus(StoreTicketStatus.OPEN)
//                        .storeStatus(StoreStatus.VALID)
//                        .avgWaitingTimeByOne(5)
//                        .build();
//                store.setMember(member);
//
//                Member admin = Member.builder()
//                        .username("admin2")
//                        .password("1234")
//                        .role(Role.ADMIN)
//                        .build();
//                memberRepository.save(member);
//                memberService.createAdmin(member);
//                memberService.createMember(member2);
//                memberService.createMember(member3);
//                memberService.createMember(member4);
//                memberService.createMember(member5);
//                memberService.createMember(member6);
//                memberService.createMember(admin);
//                storeRepository.save(store);
//            }
//        };
//     }
}
