package com.msa.rental.application.usecase;

import com.msa.rental.framework.web.dto.ClearOverdueInfoDTO;
import com.msa.rental.framework.web.dto.RentalResultOuputDTO;

public interface ClearOverdueItemUsecase {
    RentalResultOuputDTO clearOverdue(ClearOverdueInfoDTO clearOverdueInfoDTO) throws Exception;
}
