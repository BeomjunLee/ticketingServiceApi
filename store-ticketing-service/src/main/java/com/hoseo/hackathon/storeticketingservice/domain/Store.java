package com.hoseo.hackathon.storeticketingservice.domain;
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.status.ErrorStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreStatus;
import com.hoseo.hackathon.storeticketingservice.domain.status.StoreTicketStatus;
import com.hoseo.hackathon.storeticketingservice.exception.NotAuthorizedStoreException;
import com.hoseo.hackathon.storeticketingservice.exception.StoreTicketIsCloseException;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "store_id")
    private Long id;
    @Column(unique = true)
    private String name;                //이름 ->중복되면 안됨
    private String phoneNum;            //전화번호
    private String address;             //주소
    private String latitude;            //위도
    private String longitude;           //경도
    private int totalWaitingCount;       //전체 대기인원
    private int avgWaitingTimeByOne;    //한 사람당 평균 대기시간
    private int totalWaitingTime;       //전체 대기시간
    private String notice;              //공지사항
    private String companyNumber;       //사업자등록번호

    @Enumerated(EnumType.STRING)
    private StoreTicketStatus storeTicketStatus;   //가게 번호표 발급 활성화 (OPEN, CLOSE) 상태
    @Enumerated(EnumType.STRING)
    private StoreStatus storeStatus;        //가게 승인 여부 (VALID, INVALID)
    @Enumerated(EnumType.STRING)
    private ErrorStatus errorStatus;         //시스템 장애 여부 (ERROR, GOOD)


    @OneToMany(mappedBy = "store")
    private List<Member> memberList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Ticket> ticketList = new ArrayList<>();

    @Builder
    public Store(String name, String phoneNum, String address, String latitude, String longitude, int avgWaitingTimeByOne,
                 String companyNumber, StoreTicketStatus storeTicketStatus, StoreStatus storeStatus, ErrorStatus errorStatus) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.avgWaitingTimeByOne = avgWaitingTimeByOne;
        this.companyNumber = companyNumber;
        this.storeTicketStatus = storeTicketStatus;
        this.storeStatus = storeStatus;
        this.errorStatus = errorStatus;
    }

    //=========================비지니스로직=====================
    /**
     * 매장 가입
     * @param storeAdminForm 매장 폼
     */
    public static Store createStore(StoreAdminForm storeAdminForm) {
        Store store = Store.builder()
                .name(storeAdminForm.getStoreName())
                .phoneNum(storeAdminForm.getStorePhoneNum())
                .address(storeAdminForm.getStoreAddress())
                .latitude(storeAdminForm.getStoreLatitude())
                .longitude(storeAdminForm.getStoreLongitude())
                .companyNumber(storeAdminForm.getStoreCompanyNumber())
                .avgWaitingTimeByOne(5)
                .errorStatus(ErrorStatus.GOOD)
                .storeTicketStatus(StoreTicketStatus.CLOSE)
                .storeStatus(StoreStatus.INVALID)
                .build();
        return store;
    }

    /**
     * 매장 관리자 가입 승인
     */
    public void permitStoreAdmin() {
        changeStoreStatus(StoreStatus.VALID);
        super.changeCreatedDate(LocalDateTime.now());
    }

    /**
     * 매장 관리자 가입 승인 취소
     */
    public void cancelPermitStoreAdmin() {
        changeStoreStatus(StoreStatus.INVALID);
    }

    //매장 승인 상태 검증
    public void verifyStoreStatus() {
        if(getStoreStatus() != StoreStatus.VALID)
            throw new NotAuthorizedStoreException("승인 되지 않은 매장입니다");
    }

    //번호표 발급 허용 상태 검증
    public void verifyStoreTicketStatus() {
        if(getStoreTicketStatus() == StoreTicketStatus.CLOSE)
            throw new StoreTicketIsCloseException("번호표 발급이 허용되지 않았습니다");
    }

    //가게 수정
    public void changeStore(String phoneNum, String address) {
        this.phoneNum = phoneNum;
        this.address = address;
    }

    //가게 수정(관리자용)
    public void changeStoreByAdmin(String name, String phoneNum, String address, String companyNumber) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.address = address;
        this.companyNumber = companyNumber;
    }
    
    //번호표 뽑을때 Store 변경점
    public void changeStoreByTicketing(int totalWaitingCount) {
        this.totalWaitingCount = totalWaitingCount + 1;     //전체 대기인원수 설정
        this.totalWaitingTime = this.avgWaitingTimeByOne * (totalWaitingCount + 1); //전체 대기시간 설정
    }
    
    //번호표 취소, 넘기기 Store 변경점
    public void changeStoreByCancelOrNext(int totalWaitingCount) {
        this.totalWaitingCount = totalWaitingCount - 1;     //전체 대기인원수 설정
        this.totalWaitingTime = this.avgWaitingTimeByOne * (totalWaitingCount - 1); //전체 대기시간 설정
    }

    //번호표 발급 활성화 비활성화 변경
    public void changeStoreTicketStatus(StoreTicketStatus storeTicketStatus) {
        verifyStoreStatus();  //승인되지 않은 매장 체크
        this.storeTicketStatus = storeTicketStatus;
    }

    //회원가입용 번호표 발급 비활성화
    public void changeStoreTicketStatusClose() {
        this.storeTicketStatus = StoreTicketStatus.CLOSE;
    }

    //가게 승인 여부 변경
    public void changeStoreStatus(StoreStatus storeStatus) {
        this.storeStatus = storeStatus;
    }

    //가게 시스템 장애 여부 변경
    public void changeErrorStatus(ErrorStatus errorStatus) {
        this.errorStatus = errorStatus;
    }
    
//    //승인 날짜 설정
//    public void changeCreatedDate(LocalDateTime createdDate) {
//        super. = createdDate;
//    }

    //공지사항 변경
    public void changeNotice(String notice) {
        this.notice = notice;
    }

    //한사람당 대기시간 변경
    public void changeAvgWaitingTimeByOne(int avgWaitingTimeByOne) {
        this.avgWaitingTimeByOne = avgWaitingTimeByOne;
    }
}
