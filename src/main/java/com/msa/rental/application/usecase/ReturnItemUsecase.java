package com.msa.rental.application.usecase;

import com.msa.rental.framework.web.dto.RentalCardOutputDTO;
import com.msa.rental.framework.web.dto.UserItemInputDTO;

public interface ReturnItemUsecase {
    public RentalCardOutputDTO rentItem(UserItemInputDTO returnDto) throws Exception;
}
