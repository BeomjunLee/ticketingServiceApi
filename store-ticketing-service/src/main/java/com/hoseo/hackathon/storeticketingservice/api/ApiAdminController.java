package com.hoseo.hackathon.storeticketingservice.api;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.dto.*;
import com.hoseo.hackathon.storeticketingservice.domain.dto.admin.AdminMemberDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.admin.AdminMemberManageDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.admin.AdminStoreAdminDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.admin.AdminStoreManageDto;
import com.hoseo.hackathon.storeticketingservice.domain.form.AdminUpdateMemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.AdminUpdateStoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.AvgTimeForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreNoticeForm;
import com.hoseo.hackathon.storeticketingservice.domain.resource.*;
import com.hoseo.hackathon.storeticketingservice.domain.resource.admin.*;
import com.hoseo.hackathon.storeticketingservice.domain.response.Response;
import com.hoseo.hackathon.storeticketingservice.domain.status.MemberStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreStatus;
import com.hoseo.hackathon.storeticketingservice.service.AdminService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/admin", produces = MediaTypes.HAL_JSON_VALUE)
public class ApiAdminController {
    private final AdminService adminService;

//========================================가게 관리============================================
    /**
     * 관리할 가게 리스트 보기
     * 응답 : 가게이름, 전화번호, 주소, 등록일, 등록된 가게 수
     * link : 가게 번호표 관리, 가게수정, 가게관리자 정보보기, 이름으로 검색, 주소로 검색
     */
    @ApiOperation(value = "가게 목록 관리[사이트 관리자]", notes = "가게리스트를 조회하고 관리합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores")
    public ResponseEntity findStores(Pageable pageable, PagedResourcesAssembler<StoreListDto> assembler) {
        int totalEnrollStoreCount = adminService.totalEnrollStoreCount();
        AdminStoreManageDto dto = AdminStoreManageDto.builder()
                .storeList(assembler.toModel(adminService.findStores(StoreStatus.VALID, pageable), e -> new AdminStoreListResource(e)))
                .totalEnrollStoreCount(totalEnrollStoreCount)
                .build();
        //hateoas
        AdminStoreManageResource resource = new AdminStoreManageResource(dto);
        return ResponseEntity.ok(resource);
    }

    /**
     * 대기중인 번호표 리스트 관리
     * 응답 : 대기중인 회원정보,  가게 현재 상태, 총 대기인원, 총 대기시간, 공지사항, 한사람당 대기시간,
     * link : self, 대기 보류 취소 체크,  가게 번호표 활성화, 가게 번호표 비활성화, 공지사항 수정, 한사람당 대기시간 수정
     */
    @ApiOperation(value = "대기 인원 관리 + 가게 현황 관리 (번호표 관리)[사이트 관리자]", notes = "사이트 관리자가 원하는 가게의 대기 인원을 관리합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores/{store_id}")
    public ResponseEntity manageAdminStore(@PathVariable("store_id") Long store_id, Pageable pageable, PagedResourcesAssembler<WaitingMembersDto> assembler) {
        Store store = adminService.findStore(store_id);
        StoreManageDto dto = StoreManageDto.builder()
                .waitingMembers(assembler.toModel(adminService.findWaitingMembers(store_id, pageable), e -> new AdminWaitingMembersResource(e)))
                .storeTicketStatus(store.getStoreTicketStatus())
                .totalWaitingCount(store.getTotalWaitingCount())
                .totalWaitingTime(store.getTotalWaitingTime())
                .notice(store.getNotice())
                .avgWaitingTimeByOne(store.getAvgWaitingTimeByOne())
                .store_id(store.getId())
                //TODO 보류회원 결정나면 작업
                .build();
        //hateoas
        AdminWaitingMembersAndStoreManageResource resource = new AdminWaitingMembersAndStoreManageResource(dto);
        return ResponseEntity.ok(resource);
    }
    /**
     * 보류된 번호표 리스트 관리
     * 응답 ; 이름, 전화번호
     * link : 보류 ticket별 취소, 체크,
     */
    @ApiOperation(value = "보류 인원 관리[사이트 관리자]", notes = "사이트 관리자가 원하는 가게의 보류 인원 명단을 관리합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores/{store_id}/holding")
    public ResponseEntity manageHoldMembers(@PathVariable("store_id") Long store_id, Pageable pageable, PagedResourcesAssembler<HoldingMembersDto> assembler) {
        //보류회원정보
        PagedModel<EntityModel<HoldingMembersDto>> holdingMembers =
                assembler.toModel(adminService.findHoldMembers(store_id, pageable), e -> new HoldingMembersResource(e));
        
        return ResponseEntity.ok(holdingMembers);
    }

    /**
     * 번호표 보류
     */
    @ApiOperation(value = "현재 대기중인 번호표 보류[사이트 관리자]", notes = "현재 대기 회원의 번호표를 보류합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/tickets/{ticket_id}/hold-ticket")
    public ResponseEntity holdTicket(@PathVariable("ticket_id")Long ticket_id, @PathVariable("store_id")Long store_id) {
        adminService.holdTicket(store_id, ticket_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("번호표 보류 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 번호표 취소
     */
    @ApiOperation(value = "현재 대기중인 번호표 취소[사이트 관리자]", notes = "현재 대기 회원의 번호표를 취소합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/tickets/{ticket_id}/cancel-ticket")
    public ResponseEntity cancelTicket(@PathVariable("ticket_id")Long ticket_id, @PathVariable("store_id")Long store_id) {
        adminService.cancelTicketByAdmin(store_id, ticket_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("번호표 취소 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 번호표 체크
     */
    @ApiOperation(value = "현재 대기중인 번호표 체크하기[사이트 관리자]", notes = "현재 대기 회원의 번호표를 체크합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/tickets/{ticket_id}/check-ticket")
    public ResponseEntity checkTicket(@PathVariable("ticket_id")Long ticket_id, @PathVariable("store_id")Long store_id) {
        adminService.checkTicket(store_id, ticket_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("번호표 체크 성공")
                .build();
        return ResponseEntity.ok(response);
    }
    
    /**
     * 보류된 번호표 취소
     */
    @ApiOperation(value = "보류중인 번호표 취소[사이트 관리자]", notes = "현재 보류중인 회원의 번호표를 취소합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/tickets/{ticket_id}/cancel-holdingTicket")
    public ResponseEntity holdCancelTicket(@PathVariable("ticket_id")Long ticket_id, @PathVariable("store_id")Long store_id) {
        adminService.holdCancelTicket(store_id, ticket_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("보류 번호표 취소 성공")
                .build();
        return ResponseEntity.ok(response);
    }
    /**
     * 보류된 번호표 체크
     */
    @ApiOperation(value = "보류중인 번호표 체크[사이트 관리자]", notes = "현재 보류중인 회원의 번호표를 체크합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/tickets/{ticket_id}/check-holdingTicket")
    public ResponseEntity holdCheckTicket(@PathVariable("ticket_id")Long ticket_id, @PathVariable("store_id")Long store_id) {
        adminService.holdCheckTicket(store_id, ticket_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("보류 번호표 체크 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 번호표 OPEN
     */
    @ApiOperation(value = "가게 번호표 OPEN[사이트 관리자]", notes = "가게 번호표 뽑기 기능을 활성화시킵니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/open-status")
    public ResponseEntity openTicket(@PathVariable("store_id")Long store_id) {
        adminService.openTicket(store_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("가게 번호표 활성화 성공")
                .build();
        return ResponseEntity.ok(response);
    }
    /**
     * 번호표 CLOSE
     */
    @ApiOperation(value = "가게 번호표 CLOSE[사이트 관리자]", notes = "가게 번호표 뽑기 기능을 비활성화시킵니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/close-status")
    public ResponseEntity closeTicket(@PathVariable("store_id")Long store_id) {
        adminService.closeTicket(store_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("가게 번호표 비활성화 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 공지사항 수정
     */
    @ApiOperation(value = "가게 공지사항 수정[사이트 관리자]", notes = "가게의 공지사항을 수정할수 있습니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/edit-notice")
    public ResponseEntity updateNotice(@PathVariable("store_id")Long store_id, @RequestBody @Valid StoreNoticeForm form) {
        adminService.updateStoreNotice(store_id, form.getNotice());

        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("공지사항 변경 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 한사람당 대기시간 수정
     */
    @ApiOperation(value = "가게 한 사람당 대기시간 수정[사이트 관리자]", notes = "가게의 한 사람당 대기시간을 설정할 수 있습니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/edit-time")
    public ResponseEntity updateAvgWaitingTime(@PathVariable("store_id")Long store_id, @RequestBody @Valid AvgTimeForm form) {
        adminService.updateAvgTime(store_id, form.getAvgWaitingTimeByOne());

        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("한사람당 대기시간 변경 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 가게 관리자 수정
     */
    @ApiOperation(value = "가게 관리자 수정[사이트 관리자]", notes = "가게 관리자의 정보를 수정합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/stores/{store_id}/members/{member_id}")
    public ResponseEntity updateStoreAdmin(@PathVariable("store_id")Long store_id, @PathVariable("member_id")Long member_id,
                                           @RequestBody @Valid AdminUpdateStoreAdminForm form) {
        adminService.updateStoreAdmin(store_id, member_id, form);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("가게 관리자 수정 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 가게, 관리자 정보보기
     */
    @ApiOperation(value = "가게 관리자 정보보기[사이트 관리자]", notes = "가게, 관리자의 정보를 봅니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores/{store_id}/members/{member_id}")
    public ResponseEntity findStoreAdmin(@PathVariable("store_id")Long store_id, @PathVariable("member_id")Long member_id) {
        Member member = adminService.findStoreAdmin(member_id);
        Store store = adminService.findStore(store_id);
        AdminStoreAdminDto dto = AdminStoreAdminDto.builder()
                .member_username(member.getUsername())
                .member_name(member.getName())
                .member_phoneNum(member.getPhoneNum())
                .member_email(member.getEmail())
                .member_createdDate(member.getCreatedDate())
                .store_name(store.getName())
                .store_phoneNum(store.getPhoneNum())
                .store_address(store.getAddress())
                .store_companyNumber(store.getCompanyNumber())
                .store_createdDate(store.getCreatedDate())
                .build();
        return ResponseEntity.ok(dto);
    }
//==================================================회원 관리==============================================

    /**
     * 관리할 회원 리스트 보기
     * res : 총 회원수, 현재 서비스 이용자 수, 아이디, 이름, 전화번호, 이메일, 가입일, 포인트
     * 링크 : 가입일순으로보기, 이름순으로보기, 검색, 회원탈퇴, 회원수정, 번호표 취소
     */
    @ApiOperation(value = "회원 관리[사이트 관리자]", notes = "사이트 관리자가 회원 목록을 조회하며 회원을 관리합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/members")
    public ResponseEntity manageMembers(Pageable pageable, PagedResourcesAssembler<MemberListDto> assembler) {
        //회원 리스트
        AdminMemberManageDto dto = AdminMemberManageDto.builder()
                .totalMemberCount(adminService.totalMemberCount())
                .currentUsingServiceCount(adminService.currentUsingServiceCount())
                .memberList(assembler.toModel(adminService.findMembers(pageable, MemberStatus.VALID), e -> new AdminMemberListResource(e)))
                .build();
        AdminMemberManageResource resource = new AdminMemberManageResource(dto);
        return ResponseEntity.ok(resource);
    }

    /**
     * 회원 탈퇴
     */
    @ApiOperation(value = "회원 탈퇴[사이트 관리자]", notes = "사이트 관리자가 회원을 탈퇴시킵니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/members/{member_id}")
    public ResponseEntity deleteMember(@PathVariable("member_id")Long member_id) {
        adminService.deleteMember(member_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("회원 탈퇴 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 정보 보기
     */
    @ApiOperation(value = "회원 수정 정보보기[사이트 관리자]", notes = "사이트 관리자가 회원 수정하전 정보를 불러옵니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/members/{member_id}")
    public ResponseEntity updateMember(@PathVariable("member_id")Long member_id) {
        Member member = adminService.findMember(member_id);
        AdminMemberDto dto = AdminMemberDto.builder()
                .member_id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .phoneNum(member.getPhoneNum())
                .email(member.getEmail())
                .point(member.getPoint())
                .createdDate(member.getCreatedDate())
                .build();


        return ResponseEntity.ok(dto);
    }

    
    /**
     * 회원 수정
     */
    @ApiOperation(value = "회원 수정[사이트 관리자]", notes = "사이트 관리자가 회원을 수정합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/members/{member_id}")
    public ResponseEntity updateMember(@PathVariable("member_id")Long member_id, @RequestBody @Valid AdminUpdateMemberForm form) {
        adminService.updateMember(member_id, form);

        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("회원 수정 성공")
                .build();
        return ResponseEntity.ok(response);
    }


    /**
     * 비밀번호 수정
     */

    /**
     * 탈퇴후 7일지난 회원 영구삭제
     */
    @ApiOperation(value = "탈퇴 후 일주일 지난 회원 영구삭제 사이트 관리자]", notes = "사이트 관리자가 탈퇴 회원중 일주일 지난 회원들을 영구삭제합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/members/delete-members")
    public ResponseEntity deleteMembersWeekPast() {
        adminService.deleteMemberWeekPast();
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("탈퇴회원 삭제 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 번호표 취소
     */
    @ApiOperation(value = "회원 번호표 취소[사이트 관리자]", notes = "사이트 관리자가 회원의 번호표를 취소합니다(번호표를 뽑지 않은 상태일시 링크x)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/members/tickets/{ticket_id}/cancel-ticket")
    public ResponseEntity manageMembers(@PathVariable("ticket_id")Long ticket_id, Pageable pageable, PagedResourcesAssembler<MemberListDto> assembler) {
        //회원 번호표 취소
        adminService.cancelTicket(ticket_id);

        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("번호표 취소 성공")
                .build();
        return ResponseEntity.ok(response);
    }

//===========================================가입 승인===================================================
    /**
     * 가입 승인 가게 목록
     * 링크 : 가게 관리자 정보, 가입 승인, 이름으로 검색, 주소로 검색
     */
    @ApiOperation(value = "가게 승인 목록 관리[사이트 관리자]", notes = "가입 대기중인 가게 목록을 조회하고 관리합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores/waiting")
    public ResponseEntity findStoresWaitingToJoin(Pageable pageable, PagedResourcesAssembler<StoreListDto> assembler) {
        int totalEnrollStoreCount = adminService.totalEnrollStoreCount();
        AdminStoreManageDto dto = AdminStoreManageDto.builder()
                .storeList(assembler.toModel(adminService.findStores(StoreStatus.INVALID, pageable), e -> new AdminStoreWaitingToJoinListResource(e)))
                .totalEnrollStoreCount(totalEnrollStoreCount)
                .build();
        //hateoas
        AdminStoreWaitManageResource resource = new AdminStoreWaitManageResource(dto);
        return ResponseEntity.ok(resource);
    }


    /**
     * 가입 승인
     */
    @ApiOperation(value = "가게 가입 승인[사이트 관리자]", notes = "가입 대기중인 가게를 승인합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/authorization/{member_id}/permit-join")
    public ResponseEntity permitStore(@PathVariable("store_id") Long store_id, @PathVariable("member_id") Long member_id) {
        adminService.permitStoreAdmin(member_id, store_id);

        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("가입 승인 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 가입 승인 취소
     */
    @ApiOperation(value = "가게 가입 취소[사이트 관리자]", notes = "가입 대기중인 가게를 승인 취소합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/authorization/{member_id}/cancel-join")
    public ResponseEntity rejectStore(@PathVariable("store_id") Long store_id, @PathVariable("member_id") Long member_id) {
        adminService.rejectStoreAdmin(member_id, store_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("가게 승인 취소 성공")
                .build();
        return ResponseEntity.ok(response);
    }

//=============================================시스템 에러=============================================
    /**
     * 오류 접수
     */
    @ApiOperation(value = "오류 신청[사이트 관리자]", notes = "사이트 관리자한테 오류가 났다고 알림을 보냅니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/apply-errors")
    public ResponseEntity sendErrorSystem(@PathVariable("store_id")Long store_id) {
        adminService.sendErrorSystem(store_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("오류접수 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 시스템 장애 복구 완료
     */
    @ApiOperation(value = "시스템 오류 복구 완료[사이트 관리자]", notes = "오류가 생긴 가게 복구 완료로 수정")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/complete-errors")
    public ResponseEntity errorSystemComplete(@PathVariable("store_id") Long store_id) {
        adminService.completeErrorSystem(store_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("오류 해결 완료")
                .build();
        return ResponseEntity.ok(response);
    }
    
    /**
     * 시스템 장애 목록
     */
    @ApiOperation(value = "시스템 오류 목록[사이트 관리자]", notes = "오류가 생긴 가게들 목록 보기")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores/errors")
    public ResponseEntity errorSystemList(PagedResourcesAssembler<StoreErrorListDto> assembler, Pageable pageable) {
        PagedModel<AdminStoreErrorResource> adminStoreErrorManageResource = assembler.toModel(adminService.findErrorStores(pageable),
                e -> new AdminStoreErrorResource(e));
        AdminStoreErrorManageResource resource = new AdminStoreErrorManageResource(adminStoreErrorManageResource);
        return ResponseEntity.ok(resource);
    }

    /**
     * 시스템 장애 목록 대기인원순
     */
    @ApiOperation(value = "시스템 오류 목록 - 대기인원순[사이트 관리자]", notes = "오류가 생긴 가게들 목록 대기인원 순으로 보기")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores/errors/sequence")
    public ResponseEntity errorSystemListByWaiting(PagedResourcesAssembler<StoreErrorListDto> assembler, Pageable pageable) {
        PagedModel<AdminStoreErrorResource> adminStoreErrorManageResource = assembler.toModel(adminService.findErrorStoresByTotalWaitingCount(pageable),
                e -> new AdminStoreErrorResource(e));
        AdminStoreErrorManageByWaitingCountResource resource = new AdminStoreErrorManageByWaitingCountResource(adminStoreErrorManageResource);
        return ResponseEntity.ok(resource);
    }


}
