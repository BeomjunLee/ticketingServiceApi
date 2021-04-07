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
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreInfoForm;
import com.hoseo.hackathon.storeticketingservice.domain.resource.*;
import com.hoseo.hackathon.storeticketingservice.domain.resource.admin.*;
import com.hoseo.hackathon.storeticketingservice.domain.response.Response;
import com.hoseo.hackathon.storeticketingservice.domain.status.MemberStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreStatus;
import com.hoseo.hackathon.storeticketingservice.repository.condition.MemberSearchCondition;
import com.hoseo.hackathon.storeticketingservice.repository.condition.StoreSearchCondition;
import com.hoseo.hackathon.storeticketingservice.service.AdminService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
/**
 * 사이트 관리자용
 */
public class ApiAdminController {
    private final AdminService adminService;

//========================================매장 관리============================================

    /**
     * 관리할 매장 리스트 보기 (아이디, 이름, 전화번호 검색가능)
     * @param pageable 페이징
     * @param assembler 페이징 관련 hateoas
     * @return 매장 관리 + hateoas link
     */
    @ApiOperation(value = "매장 목록 관리[사이트 관리자]", notes = "매장리스트를 조회하고 관리합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores")
    public ResponseEntity findStores(Pageable pageable,
                                     PagedResourcesAssembler<StoreListDto> assembler,
                                     StoreSearchCondition condition) {

        int totalEnrollStoreCount = adminService.totalEnrollStoreCount();
        AdminStoreManageDto dto = AdminStoreManageDto.builder()
                .storeList(assembler.toModel(adminService.findStores(StoreStatus.VALID, pageable, condition), e -> new AdminStoreListResource(e)))
                .totalEnrollStoreCount(totalEnrollStoreCount)
                .build();
        //hateoas
        AdminStoreManageResource resource = new AdminStoreManageResource(dto);
        return ResponseEntity.ok(resource);
    }

    /**
     * 매장 번호표 리스트 관리 (보류 번호표는 x)
     * @param store_id 매장 고유 id 값
     * @param pageable 페이징
     * @param assembler 페이징 관련 hateoas
     * @return 매장 관리 + hateoas link
     */
    @ApiOperation(value = "대기 인원 관리 + 매장 현황 관리 (번호표 관리)[사이트 관리자]", notes = "사이트 관리자가 원하는 매장의 대기 인원을 관리합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores/{store_id}")
    public ResponseEntity manageAdminStore(@PathVariable("store_id") Long store_id,
                                           Pageable pageable,
                                           PagedResourcesAssembler<WaitingMembersDto> assembler) {

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
     * @param store_id
     * @param pageable 페이징
     * @param assembler 페이징 관련 hateoas
     * @return 보류 회원 dto + hateoas link
     */
    @ApiOperation(value = "보류 인원 관리[사이트 관리자]", notes = "사이트 관리자가 원하는 매장의 보류 인원 명단을 관리합니다")
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
     * @param ticket_id 번호표 고유 id 값
     * @param store_id 매장 고유 id 값
     * @return http response
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
     * @param ticket_id 번호표 고유 id 값
     * @param store_id 매장 고유 id 값
     * @return http response
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
     * @param ticket_id 번호표 고유 id 값
     * @param store_id 매장 고유 id 값
     * @return http response
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
     * 보류 중인 번호표 취소
     * @param ticket_id 번호표 고유 id 값
     * @param store_id 매장 고유 id 값
     * @return http response
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
     * 보류중인 번호표 체크
     * @param ticket_id 번호표 고유 id 값
     * @param store_id 매장 고유 id 값
     * @return http response
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
     * 번호표 발급 가능 상태 -> OPEN
     * @param store_id 매장 고유 id 값
     * @return http response
     */
    @ApiOperation(value = "매장 번호표 OPEN[사이트 관리자]", notes = "매장 번호표 뽑기 기능을 활성화시킵니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/open-status")
    public ResponseEntity openTicket(@PathVariable("store_id")Long store_id) {
        adminService.openTicket(store_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("매장 번호표 활성화 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 번호표 발급 가능 상태 -> CLOSE
     * @param store_id 매장 고유 id 값
     * @return http response
     */
    @ApiOperation(value = "매장 번호표 CLOSE[사이트 관리자]", notes = "매장 번호표 뽑기 기능을 비활성화시킵니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/close-status")
    public ResponseEntity closeTicket(@PathVariable("store_id")Long store_id) {
        adminService.closeTicket(store_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("매장 번호표 비활성화 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 매장 상태 정보 수정 (공지사항, 한사람당 예상 대기시간)
     * @param store_id 매장 고유 id 값
     * @param form 매장 상태 정보 form
     * @return http response
     */
    @ApiOperation(value = "매장 공지사항 수정[사이트 관리자]", notes = "매장의 공지사항, 평균 대기시간을 수정할 수 있습니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/stores/{store_id}/edit")
    public ResponseEntity updateInfo(@PathVariable("store_id")Long store_id, @RequestBody @Valid StoreInfoForm form) {
        adminService.updateStoreInfo(store_id, form);

        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("매장 상태 정보 변경 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 매장 관리자 정보 수정
     * @param store_id 매장 고유 id 값
     * @param member_id 회원 고유 id 값
     * @param form (사이트 관리자 전용)매장 관리자 정보 form
     * @return http response
     */
    @ApiOperation(value = "매장 관리자 수정[사이트 관리자]", notes = "매장 관리자의 정보를 수정합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/stores/{store_id}/members/{member_id}")
    public ResponseEntity updateStoreAdmin(@PathVariable("store_id")Long store_id, @PathVariable("member_id")Long member_id,
                                           @RequestBody @Valid AdminUpdateStoreAdminForm form) {
        adminService.updateStoreAdmin(store_id, member_id, form);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("매장 관리자 수정 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 매장, 관리자 정보보기
     * @param store_id 매장 고유 id 값
     * @param member_id 회원 고유 id 값
     * @return http response
     */
    @ApiOperation(value = "매장 관리자 정보보기[사이트 관리자]", notes = "매장, 관리자의 정보를 봅니다")
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
     * @param assembler 페이징 관련 hateoas
     * @return 회원 관리 dto + hateoas link
     */
    @ApiOperation(value = "회원 관리[사이트 관리자]", notes = "사이트 관리자가 회원 목록을 조회하며 회원을 관리합니다")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/members")
    public ResponseEntity manageMembers(Pageable pageable,
                                        PagedResourcesAssembler<MemberListDto> assembler,
                                        MemberSearchCondition condition) {

        AdminMemberManageDto dto = AdminMemberManageDto.builder()
                .totalMemberCount(adminService.totalMemberCount())
                .currentUsingServiceCount(adminService.currentUsingServiceCount())
                .memberList(assembler.toModel(adminService.findMembers(pageable, MemberStatus.VALID, condition), e -> new AdminMemberListResource(e)))
                .build();
        AdminMemberManageResource resource = new AdminMemberManageResource(dto);
        return ResponseEntity.ok(resource);
    }

    /**
     * 회원 탈퇴
     * @param member_id 회원 고유 id 값
     * @return http response
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
     * @param member_id 회원 고유 id 값
     * @return (사이트 관리자 전용) 회원 정보 dto
     */
    @ApiOperation(value = "회원 수정 정보보기[사이트 관리자]", notes = "사이트 관리자가 회원의 정보를 불러옵니다")
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
     * @param member_id 회원 고유 id 값
     * @param form (사이트 관리자 전용) 회원 수정 form
     * @return http response
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
     * 탈퇴 후 7일 지난 회원 영구 삭제
     * @return http response
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
     * 번호표 취소 (ticket_id 이용)
     * @param ticket_id 번호표 고유 id 값
     * @return http response
     */
    @ApiOperation(value = "회원 번호표 취소[사이트 관리자]", notes = "사이트 관리자가 회원의 번호표를 취소합니다(번호표를 뽑지 않은 상태일시 링크x)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/members/tickets/{ticket_id}/cancel-ticket")
    public ResponseEntity manageMembers(@PathVariable("ticket_id")Long ticket_id) {
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
     * 가입 승인 신청 매장 목록
     * @param assembler 페이징 관련 hateoas
     * @return 매장 관리 dto + hateoas link
     */
    @ApiOperation(value = "매장 승인 목록 관리[사이트 관리자]", notes = "가입 대기중인 매장 목록을 조회하고 관리합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores/waiting")
    public ResponseEntity findStoresWaitingToJoin(Pageable pageable,
                                                  PagedResourcesAssembler<StoreListDto> assembler,
                                                  StoreSearchCondition condition) {

        int totalEnrollStoreCount = adminService.totalEnrollStoreCount();
        AdminStoreManageDto dto = AdminStoreManageDto.builder()
                .storeList(assembler.toModel(adminService.findStores(StoreStatus.INVALID, pageable, condition), e -> new AdminStoreWaitingToJoinListResource(e)))
                .totalEnrollStoreCount(totalEnrollStoreCount)
                .build();
        //hateoas
        AdminStoreWaitManageResource resource = new AdminStoreWaitManageResource(dto);
        return ResponseEntity.ok(resource);
    }

    /**
     * 매장 가입 승인
     * @param store_id 매장 고유 id 값
     * @param member_id 회원 id 값
     * @return http response
     */
    @ApiOperation(value = "매장 가입 승인[사이트 관리자]", notes = "가입 대기중인 매장을 승인합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/members/{member_id}/permit-join")
    public ResponseEntity permitStore(@PathVariable("store_id") Long store_id, @PathVariable("member_id") Long member_id) {
        adminService.permitStoreAdmin(member_id, store_id);

        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("매장 가입 승인 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 매장 가입 승인 취소
     * @param store_id 매장 고유 id 값
     * @param member_id 회원 id 값
     * @return http response
     */
    @ApiOperation(value = "매장 가입 취소[사이트 관리자]", notes = "가입 대기중인 매장의 승인을 취소합니다")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stores/{store_id}/members/{member_id}/cancel-join")
    public ResponseEntity rejectStore(@PathVariable("store_id") Long store_id, @PathVariable("member_id") Long member_id) {
        adminService.rejectStoreAdmin(member_id, store_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("매장 승인 취소 성공")
                .build();
        return ResponseEntity.ok(response);
    }

//=============================================시스템 에러=============================================

    /**
     * 오류 접수
     * @param store_id 매장 고유 id 값
     * @return http response
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
     * 시스템 오류 복구 완료 처리
     * @param store_id 매장 고유 id 값
     * @return http response
     */
    @ApiOperation(value = "시스템 오류 복구 완료[사이트 관리자]", notes = "오류가 생긴 매장 상태를 복구 완료로 수정")
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
     * 시스템 오류 매장 목록
     * @param assembler 페이징 관련 hateoas
     * @param pageable 페이징
     */
    @ApiOperation(value = "시스템 오류 목록[사이트 관리자]", notes = "오류가 생긴 매장들 목록 보기")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores/errors")
    public ResponseEntity errorSystemList(PagedResourcesAssembler<StoreErrorListDto> assembler, Pageable pageable) {
        PagedModel<AdminStoreErrorResource> adminStoreErrorManageResource = assembler.toModel(adminService.findErrorStores(pageable),
                e -> new AdminStoreErrorResource(e));
        AdminStoreErrorManageResource resource = new AdminStoreErrorManageResource(adminStoreErrorManageResource);
        return ResponseEntity.ok(resource);
    }

    /**
     * 시스템 오류 매장 목록 대기 인원 순
     * @param assembler 페이징 관련 hateoas
     * @param pageable 페이징
     */
    @ApiOperation(value = "시스템 오류 목록 - 대기인원순[사이트 관리자]", notes = "오류가 생긴 매장들 목록 대기인원 순으로 보기")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stores/errors/sequence")
    public ResponseEntity errorSystemListByWaiting(PagedResourcesAssembler<StoreErrorListDto> assembler, Pageable pageable) {
        PagedModel<AdminStoreErrorResource> adminStoreErrorManageResource = assembler.toModel(adminService.findErrorStoresByTotalWaitingCount(pageable),
                e -> new AdminStoreErrorResource(e));
        AdminStoreErrorManageByWaitingCountResource resource = new AdminStoreErrorManageByWaitingCountResource(adminStoreErrorManageResource);
        return ResponseEntity.ok(resource);
    }


}
