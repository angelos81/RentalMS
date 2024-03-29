package com.msa.rental.application.inputport;

import com.msa.rental.application.outputport.EventOutputPort;
import com.msa.rental.application.outputport.RentalCardOutputPort;
import com.msa.rental.application.usecase.ReturnItemUsecase;
import com.msa.rental.domain.model.RentalCard;
import com.msa.rental.domain.model.event.ItemReturned;
import com.msa.rental.domain.model.vo.Item;
import com.msa.rental.framework.web.dto.RentalCardOutputDTO;
import com.msa.rental.framework.web.dto.UserItemInputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class ReturnItemInputPort implements ReturnItemUsecase {

    private final RentalCardOutputPort rentalCardOutputPort;
    private final EventOutputPort eventOutputPort;

    @Override
    public RentalCardOutputDTO returnItem(UserItemInputDTO returnDto) throws Exception {
        // rentalCard 검색 -> 없으면 Exception 처리, 있으면 도서 반납
        RentalCard rentalCard = rentalCardOutputPort.loadRentalCard(returnDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카드가 존재하지 않습니다."));

        Item returnItem = new Item(returnDto.getItemId(), returnDto.getItemTitle());
        rentalCard.returnItem(returnItem, LocalDate.now());

        /*** 도메인 이벤트 생성 및 발행 ***/
        // 반납 이벤트 생성
        ItemReturned itemReturnEvent = RentalCard.createItemReturnEvent(rentalCard.getMember(), returnItem, 10L);
        // 반납 이벤트 발행
        eventOutputPort.occurReturnEvent(itemReturnEvent);


        return RentalCardOutputDTO.mapToDTO(rentalCard);
    }
}
