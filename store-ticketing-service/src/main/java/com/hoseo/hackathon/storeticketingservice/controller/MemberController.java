package com.hoseo.hackathon.storeticketingservice.controller;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.dto.MemberDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.MyTicketDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreAdminDto;
import com.hoseo.hackathon.storeticketingservice.domain.form.*;
import com.hoseo.hackathon.storeticketingservice.domain.resource.MemberResource;
import com.hoseo.hackathon.storeticketingservice.domain.resource.MyTicketResource;
import com.hoseo.hackathon.storeticketingservice.domain.resource.StoreAdminResource;
import com.hoseo.hackathon.storeticketingservice.domain.response.LoginResponse;
import com.hoseo.hackathon.storeticketingservice.domain.response.Response;
import com.hoseo.hackathon.storeticketingservice.domain.response.ResultStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.MemberRole;
import com.hoseo.hackathon.storeticketingservice.security.JwtProvider;
import com.hoseo.hackathon.storeticketingservice.service.MemberService;
import com.hoseo.hackathon.storeticketingservice.service.StoreService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;

import static org.reflections.Reflections.log;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/members", produces = MediaTypes.HAL_JSON_VALUE)
public class MemberController {

    private final MemberService memberService;
    private final StoreService storeService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * test
     */
    @GetMapping("/test")
    public ResponseEntity member() {
        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(201)
                .message("테스트 성공")
                .build());
    }

    /**
     * 로그인
     * @param LoginForm 로그인 요청 form
     * @return json response
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginForm LoginForm) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(LoginForm.getUsername(), LoginForm.getPassword());

        //아이디 체크는 Authentication 에 사용자 입력 아이디, 비번을 넣어줘야지 작동
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        log.info(authentication + " 로그인 처리 authentication");

        //jwt accessToken & refreshToken 발급
        String accessToken = jwtProvider.generateToken(authentication, false);
        String refreshToken = jwtProvider.generateToken(authentication, true);

        //회원 DB에 refreshToken 저장
        memberService.findMemberAndSaveRefreshToken(authentication.getName(), refreshToken);

        LoginResponse response = LoginResponse.builder()
                .status(HttpStatus.OK.value())
                .message("로그인 성공")
                .accessToken(accessToken)
                .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                .refreshToken(refreshToken)
                .issuedAt(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * refreshToken 으로 accessToken 재발급
     * @param refreshTokenForm accessToken 재발급 요청 dto
     * @return json response
     */
    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(@RequestBody RefreshTokenForm refreshTokenForm) {
        return ResponseEntity.ok(memberService.refreshToken(refreshTokenForm));
    }

    /**
     * [회원]회원 가입
     * @param memberForm 회원가입 form
     * @return json response
     */
    @ApiOperation(value = "일반 회원 가입[누구나]", notes = "회원을 서비스에 가입시킵니다")
    @PostMapping("/new")
    public ResponseEntity signUpMember(@Valid @RequestBody MemberForm memberForm) {
        memberService.createMember(memberForm);

        URI createUri = linkTo(MemberController.class).slash("new").toUri();
        return ResponseEntity.created(createUri).body(Response.builder()
                                                        .result(ResultStatus.SUCCESS)
                                                        .status(201)
                                                        .message("회원가입 성공")
                                                        .build());
    }

    /**
     * [매장 관리자] 가입
     * @param storeAdminForm 매장관리자 form
     * @return json response
     */
    @ApiOperation(value = "매장 관리자 회원 가입[누구나]", notes = "매장 사장님을 서비스에 가입시킵니다")
    @PostMapping("/storeAdmin/new")
    public ResponseEntity signUpAdmin(@Valid @RequestBody StoreAdminForm storeAdminForm) {
        memberService.createStoreAdmin(storeAdminForm);
        URI createUri = linkTo(MemberController.class).slash("admin/new").toUri();

        return ResponseEntity.created(createUri).body(Response.builder()
                                                        .result(ResultStatus.SUCCESS)
                                                        .status(201)
                                                        .message("관리자 가입 성공")
                                                        .build());
    }

    /**
     * [회원] 정보보기
     * @param principal access_token 에서 추출한 회원 정보
     * @return json response
     */
    @ApiOperation(value = "회원 정보 조회[회원, 매장 관리자]", notes = "회원정보를 조회합니다")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_STORE_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity myInfo(Principal principal) {
        Member member = memberService.findByUsername(principal.getName());
        if (member.getRoles().contains(MemberRole.USER)){    //회원
            MemberDto dto = MemberDto.builder()
                    .username(member.getUsername())
                    .name(member.getName())
                    .phoneNum(member.getPhoneNum())
                    .email(member.getEmail())
                    .point(member.getPoint())
                    .build();
            MemberResource resource = new MemberResource(dto);
            return ResponseEntity.ok(resource);

            }else if (member.getRoles().contains(MemberRole.STORE_ADMIN)) {  //가게 관리자
            Store store = storeService.findStore(member.getUsername());
            StoreAdminDto dto = StoreAdminDto.builder()
                    .member_id(member.getId())
                    .member_username(member.getUsername())
                    .member_name(member.getName())
                    .member_phoneNum(member.getPhoneNum())
                    .member_email(member.getEmail())
                    .store_id(store.getId())
                    .store_name(store.getName())
                    .store_address(store.getAddress())
                    .store_phoneNum(store.getPhoneNum())
                    .store_companyNumber(store.getCompanyNumber())
                    .store_status(store.getStoreStatus().getStatus())
                    .build();
            StoreAdminResource resource = new StoreAdminResource(dto);
            return ResponseEntity.ok(resource);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.builder()
                                                                    .result(ResultStatus.FAIL)
                                                                    .status(404)
                                                                    .message("회원 조회 오류")
                                                                    .build());
    }

    /**
     * [회원] 정보 수정
     * @param principal access_token 에서 추출한 회원 정보
     * @param memberForm 회원 form
     * @param storeForm 매장 관리자 form
     * @return json response
     */
    @ApiOperation(value = "회원 정보 수정[회원, 매장 관리자]", notes = "회원정보를 수정합니다")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_STORE_ADMIN')")
    @PatchMapping("/me")
    public ResponseEntity updateMyInfo(Principal principal,
                                       @RequestBody @Valid UpdateMemberForm memberForm,
                                       @RequestBody @Valid UpdateStoreAdminForm storeForm) {
        Member member = memberService.findByUsername(principal.getName());

        if (member.getRoles().contains(MemberRole.USER)){    //회원
            memberService.updateMember(principal.getName(), memberForm);
            return ResponseEntity.ok(Response.builder()
                    .result(ResultStatus.SUCCESS)
                    .status(200)
                    .message("회원 수정 완료")
                    .build());

        }else if (member.getRoles().contains(MemberRole.STORE_ADMIN)) {  //가게 관리자
            memberService.updateStoreAdmin(principal.getName(), storeForm);
            return ResponseEntity.ok(Response.builder()
                    .result(ResultStatus.SUCCESS)
                    .status(200)
                    .message("회원 수정 완료")
                    .build());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.builder()
                                                                    .result(ResultStatus.FAIL)
                                                                    .status(404)
                                                                    .message("회원 수정 오류")
                                                                    .build());
    }

    /**
     * 비밀번호 변경
     * @param principal access_token 에서 추출한 회원 정보
     * @param form 비밀번호 변경 form
     * @return json response
     */
    @ApiOperation(value = "비밀번호 수정[회원, 매장 관리자]", notes = "비밀번호를 수정합니다")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_STORE_ADMIN')")
    @PatchMapping("/me/change-password")
    public ResponseEntity changePassword(Principal principal, @RequestBody @Valid UpdatePasswordForm form) {
        memberService.changePassword(principal.getName(), form.getCurrentPassword(), form.getNewPassword());

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("비밀번호 변경 완료")
                .build());
    }


    /**
     * [회원] 번호표 보기
     * @param principal access_token 에서 추출한 회원 정보
     * @return json response
     */
    @ApiOperation(value = "번호표 조회[회원]", notes = "회원이 번호표를 조회합니다")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/tickets")
    public ResponseEntity myTicket(Principal principal) {
        Ticket ticket = storeService.findMyTicket(principal.getName());
        Store store = storeService.findValidStoreById(ticket.getStore().getId());
        
        MyTicketDto dto = MyTicketDto.builder()
                .name(store.getName())
                .phoneNum(store.getPhoneNum())
                .notice(store.getNotice())
                .totalWaitingCount(store.getTotalWaitingCount())
                .peopleCount(ticket.getPeopleCount())
                .waitingNum(ticket.getWaitingNum())
                .waitingTime(ticket.getWaitingTime())
                .build();

        return ResponseEntity.ok(new MyTicketResource(dto));
    }


    /**
     * [회원] 번호표 취소
     * @param principal access_token 에서 추출한 회원 정보
     * @return json response
     */
    @ApiOperation(value = "번호표 취소[회원]", notes = "회원이 뽑은 번호표를 취소합니다")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/tickets/cancel-ticket")
    public ResponseEntity cancelMyTicket(Principal principal) {
        storeService.cancelTicket(principal.getName());

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("번호표 취소 성공")
                .build());
    }
}
