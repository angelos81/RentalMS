package com.msa.rental.application.outputport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.msa.rental.domain.model.event.ItemRented;
import com.msa.rental.domain.model.event.ItemReturned;
import com.msa.rental.domain.model.event.OverdueCleared;

public interface EventOutputPort {
    public void occurRentalEvent(ItemRented rentalItemEvent) throws JsonProcessingException;

    public void occurReturnEvent(ItemReturned itemReturnedEvent) throws JsonProcessingException;

    public void occurOverdueClearedEvent(OverdueCleared overdueCleared) throws JsonProcessingException;
}
