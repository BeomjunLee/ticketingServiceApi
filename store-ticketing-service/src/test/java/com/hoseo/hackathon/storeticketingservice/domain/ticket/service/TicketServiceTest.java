package com.hoseo.hackathon.storeticketingservice.domain.ticket.service;

import com.hoseo.hackathon.storeticketingservice.domain.FormBuilder;
import com.hoseo.hackathon.storeticketingservice.domain.member.MemberBuilder;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.MemberForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.dto.form.StoreAdminForm;
import com.hoseo.hackathon.storeticketingservice.domain.member.entity.Member;
import com.hoseo.hackathon.storeticketingservice.domain.member.exception.DuplicateUsernameException;
import com.hoseo.hackathon.storeticketingservice.domain.member.repository.MemberQueryRepository;
import com.hoseo.hackathon.storeticketingservice.domain.store.StoreBuilder;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.enums.StoreStatus;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.enums.StoreTicketStatus;
import com.hoseo.hackathon.storeticketingservice.domain.store.repository.StoreRepository;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.TicketBuilder;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.dto.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.enums.TicketStatus;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.exception.DuplicateTicketingException;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.repository.TicketRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private MemberQueryRepository memberQueryRepository;

    private TicketForm ticketForm;
    private Store store;
    private Member member;
    private Ticket ticket;

    @BeforeEach
    public void setUp() {
        ticketForm = FormBuilder.ticketFormBuild();

        store = StoreBuilder.build(StoreAdminForm.builder().build());
        store.changeStoreStatus(StoreStatus.VALID);
        store.changeStoreTicketStatus(StoreTicketStatus.OPEN);

        member = MemberBuilder.memberBuild(MemberForm.builder().build());

        ticket = TicketBuilder.build(ticketForm, store, member);
    }

    @Test
    @DisplayName("번호표 발급 성공")
    public void createTicket() throws Exception{
        //given
        when(memberQueryRepository.findMemberJoinTicketByUsername(any())).thenReturn(Optional.of(member));
        when(storeRepository.findById(any())).thenReturn(Optional.of(store));
        when(ticketRepository.save(any())).thenReturn(ticket);

        //when
        Ticket savedTicket = ticketService.createTicket(ticketForm, store.getId(), any());

        //then
        assertThat(savedTicket.getStore()).usingRecursiveComparison().isEqualTo(store);
        assertThat(savedTicket.getMember()).usingRecursiveComparison().isEqualTo(member);
        assertThat(savedTicket.getStore().getTotalWaitingCount()).isEqualTo(1);
        assertThat(savedTicket.getStore().getTotalWaitingTime()).isEqualTo(5);
        assertThat(savedTicket.getPeopleCount()).isEqualTo(ticketForm.getPeopleCount());
        assertThat(savedTicket.getWaitingTime()).isEqualTo(5);
        assertThat(savedTicket.getWaitingNum()).isEqualTo(1);
    }

    @Test
    @DisplayName("번호표 발급 실패 (중복 발급)")
    public void createTicketFail() throws Exception{
        //given
        when(memberQueryRepository.findMemberJoinTicketByUsername(any())).thenReturn(Optional.of(member));
        when(storeRepository.findById(any())).thenReturn(Optional.of(store));
        when(ticketRepository.save(any())).thenReturn(ticket);

        //when, then
        assertThatThrownBy(() -> {
            ticketService.createTicket(ticketForm, store.getId(), any());
            ticketService.createTicket(ticketForm, store.getId(), any());
        }).isInstanceOf(DuplicateTicketingException.class).hasMessageContaining("이미 번호표를 가지고 있습니다");
    }

    @Test
    @DisplayName("나의 번호표 취소")
    public void cancelMyTicket() throws Exception{
        //given
        when(ticketRepository.findTicketJoinMemberJoinStoreByUsernameAndStatus(any(), any())).thenReturn(Optional.of(ticket));

        //when


        //then
    }
}
