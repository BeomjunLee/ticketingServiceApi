package com.hoseo.hackathon.storeticketingservice.api;

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
import com.hoseo.hackathon.storeticketingservice.domain.response.Response;
import com.hoseo.hackathon.storeticketingservice.domain.status.Role;
import com.hoseo.hackathon.storeticketingservice.service.MemberService;
import com.hoseo.hackathon.storeticketingservice.service.StoreService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/members", produces = MediaTypes.HAL_JSON_VALUE)
public class ApiMemberController {

    private final MemberService memberService;
    private final StoreService storeService;
    /**
     * test
     */
    @GetMapping("/test")
    public ResponseEntity member() {
        Response response = Response.builder()
                .result("success")
                .status(201)
                .message("테스트 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * [회원] 가입
     */
    @ApiOperation(value = "일반 회원 가입[누구나]", notes = "회원을 서비스에 가입시킵니다")
    @PostMapping("/new")
    public ResponseEntity signUpMember(@Valid @RequestBody MemberForm memberForm) {
        Member member = Member.builder()
                .username(memberForm.getUsername())
                .password(memberForm.getPassword())
                .name(memberForm.getName())
                .phoneNum(memberForm.getPhoneNum())
                .email(memberForm.getEmail())
                .createdDate(LocalDateTime.now())
                .deletedDate(null)  //탈퇴일은 가입시 null(재가입시 null 로 바꿔야돼서)
                .build();
        memberService.createMember(member);
        Response response = Response.builder()
                .result("success")
                .status(201)
                .message("회원가입 성공")
                .build();
        URI createUri = linkTo(ApiMemberController.class).slash("new").toUri();
        return ResponseEntity.created(createUri).body(response);
    }

    /**
     * [관리자] 가입
     */
    @ApiOperation(value = "가게 관리자 회원 가입[누구나]", notes = "가게 사장님을 서비스에 가입시킵니다")
    @PostMapping("/admin/new")
    public ResponseEntity signUpAdmin(@Valid @RequestBody StoreAdminForm storeAdminForm) {
        Member member = Member.builder()//회원
                .username(storeAdminForm.getMemberUsername())
                .password(storeAdminForm.getMemberPassword())
                .name(storeAdminForm.getMemberName())
                .phoneNum(storeAdminForm.getMemberPhoneNum())
                .email(storeAdminForm.getMemberEmail())
                .createdDate(LocalDateTime.now())
                .build();
        Store store = Store.builder()//가게
                .name(storeAdminForm.getStoreName())
                .phoneNum(storeAdminForm.getStorePhoneNum())
                .address(storeAdminForm.getStoreAddress())
                //TODO 주소를 위도, 경도로 바꿔야됨
                .latitude(storeAdminForm.getStoreLatitude())
                .longitude(storeAdminForm.getStoreLongitude())
                .companyNumber(storeAdminForm.getStoreCompanyNumber())
                .member(member)
                .build();

        memberService.createStoreAdmin(member, store);
        Response response = Response.builder()
                .result("success")
                .status(201)
                .message("관리자 가입 성공")
                .build();
        URI createUri = linkTo(ApiMemberController.class).slash("admin/new").toUri();
        return ResponseEntity.created(createUri).body(response);
    }

    /**
     * [회원] 정보보기
     */
    @ApiOperation(value = "회원 정보 조회[회원, 가게관리자]", notes = "회원정보를 조회합니다")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_STORE_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity myInfo(Principal principal) {
        Member member = memberService.findByUsername(principal.getName());
        if (member.getRole().equals(Role.USER)){    //회원
            MemberDto dto = MemberDto.builder()
                    .username(member.getUsername())
                    .name(member.getName())
                    .phoneNum(member.getPhoneNum())
                    .email(member.getEmail())
                    .point(member.getPoint())
                    .build();
            MemberResource resource = new MemberResource(dto);
            return ResponseEntity.ok(resource);

            }else if (member.getRole().equals(Role.STORE_ADMIN)) {  //가게 관리자
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

        Response response = Response.builder()
                .result("fail")
                .status(404)
                .message("회원 조회 오류")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    /**
     * [회원] 정보 수정
     */
    @ApiOperation(value = "회원 정보 수정[회원, 가게관리자]", notes = "회원정보를 수정합니다")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_STORE_ADMIN')")
    @PatchMapping("/me")
    public ResponseEntity updateMyInfo(Principal principal,
                                       @RequestBody @Valid UpdateMemberForm memberForm,
                                       @RequestBody @Valid UpdateStoreAdminForm storeForm) {
        Member member = memberService.findByUsername(principal.getName());
        if (member.getRole().equals(Role.USER)){    //회원
            memberService.updateMember(principal.getName(), memberForm);
            Response response = Response.builder()
                    .result("success")
                    .status(200)
                    .message("회원 수정 완료")
                    .build();
            return ResponseEntity.ok(response);

        }else if (member.getRole().equals(Role.STORE_ADMIN)) {  //가게 관리자
            memberService.updateStoreAdmin(principal.getName(), storeForm);
            Response response = Response.builder()
                    .result("success")
                    .status(200)
                    .message("회원 수정 완료")
                    .build();
            return ResponseEntity.ok(response);
        }

        Response response = Response.builder()
                .result("fail")
                .status(404)
                .message("회원 수정 오류")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 비밀번호 변경
     */
    @ApiOperation(value = "비밀번호 수정[회원, 가게관리자]", notes = "비밀번호를 수정합니다")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_STORE_ADMIN')")
    @PatchMapping("/me/change-password")
    public ResponseEntity changePassword(Principal principal, @RequestBody @Valid UpdatePasswordForm form) {
        memberService.changePassword(principal.getName(), form.getCurrentPassword(), form.getNewPassword());
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("비밀번호 변경 완료")
                .build();
        return ResponseEntity.ok(response);
    }


    /**
     * [회원] 번호표 보기
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
        MyTicketResource resource = new MyTicketResource(dto);
        return ResponseEntity.ok(resource);
    }

    /**
     * [회원] 번호표 취소
     */
    @ApiOperation(value = "번호표 취소[회원]", notes = "회원이 뽑은 번호표를 취소합니다")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/tickets/cancel-ticket")
    public ResponseEntity cancelMyTicket(Principal principal) {
        storeService.cancelTicket(principal.getName());
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("번호표 취소 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * [회원] 포인트 기부하기
     */
}
