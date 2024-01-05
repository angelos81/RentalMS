package com.msa.rental.application.inputport;

import com.msa.rental.application.outputport.RentalCardOutputPort;
import com.msa.rental.application.usecase.RentItemUsecase;
import com.msa.rental.domain.model.RentalCard;
import com.msa.rental.domain.model.vo.IDName;
import com.msa.rental.domain.model.vo.Item;
import com.msa.rental.framework.web.dto.RentalCardOutputDTO;
import com.msa.rental.framework.web.dto.UserItemInputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RentItemInputPort implements RentItemUsecase {
    
    private final RentalCardOutputPort rentalCardOutputPort;
    
    @Override
    public RentalCardOutputDTO rentItem(UserItemInputDTO rental) throws Exception {
        // RentalCard가 없는 경우는 생성 후 RentalCard 리턴
        RentalCard rentalCard = rentalCardOutputPort.loadRentalCard(rental.userId)
                .orElseGet(() -> RentalCard.createRentalCard(new IDName(rental.userId, rental.getUserNm())));

        Item newItem = new Item(rental.itemId, rental.itemTitle);
        rentalCard.rentItem(newItem);
        
        // Spring JPA의 영속성 컨텍스트로 인해 save를 호출하지 않아도 데이터 저장
        //RentalCard save = rentalCardOutputPort.save(rentalCard);
        //return RentalCardOutputDTO.mapToDTO(save);
        
        return RentalCardOutputDTO.mapToDTO(rentalCard);
    }
}
