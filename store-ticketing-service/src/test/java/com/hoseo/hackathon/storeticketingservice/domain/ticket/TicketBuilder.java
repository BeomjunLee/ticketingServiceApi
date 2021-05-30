package com.hoseo.hackathon.storeticketingservice.domain.ticket;

import com.hoseo.hackathon.storeticketingservice.domain.member.entity.Member;
import com.hoseo.hackathon.storeticketingservice.domain.store.entity.Store;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.dto.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.entity.Ticket;

public class TicketBuilder {

    public static Ticket build(TicketForm ticketForm, Store store, Member member) {
        return Ticket.createTicket(ticketForm, store, member);
    }


}
