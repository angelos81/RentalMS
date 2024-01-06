package com.msa.rental.framework.web;

import com.msa.rental.application.usecase.*;
import com.msa.rental.framework.web.dto.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RentalController {

    private final RentItemUsecase rentItemUsecase;
    private final ReturnItemUsecase returnItemUsecase;
    private final CreateRentalCardUsecase createRentalCardUsecase;
    private final OverdueItemUsecase overdueItemUsecase;
    private final ClearOverdueItemUsecase clearOverdueItemUsecase;
    private final InquiryUsecase inquiryUsecase;

    @ApiOperation(value = "도서카드 생성",notes = "사용자정보 -> 도서카드정보")
    @PostMapping("/RentalCard/")
    public ResponseEntity<RentalCardOutputDTO> createRentalCard(@RequestBody UserInputDTO userInputDTO) {
        RentalCardOutputDTO createRentalCard = createRentalCardUsecase.createRentalCard(userInputDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createRentalCard);
    }

    @ApiOperation(value = "도서카드 조회",notes = "사용자정보(id) -> 도서카드정보")
    @GetMapping("/RentalCard/{userId}")
    public ResponseEntity<RentalCardOutputDTO> getRentalCard(@PathVariable String userId) {
        Optional<RentalCardOutputDTO> rentalCard = inquiryUsecase.getRentalCard(new UserInputDTO(userId, ""));
        return ResponseEntity.status(HttpStatus.OK).body(rentalCard.get());
    }

    @ApiOperation(value = "대여도서목록 조회",notes = "사용자정보(id) -> 대여도서목록 조회")
    @GetMapping("/RentalCard/{userId}/rentbook")
    public ResponseEntity<List<RentItemOutputDTO>> getAllRentItem(@PathVariable String userId) {
        Optional<List<RentItemOutputDTO>> allRentItem = inquiryUsecase.getAllRentItem(new UserInputDTO(userId, ""));
        return ResponseEntity.status(HttpStatus.OK).body(allRentItem.get());
    }

    @ApiOperation(value = "반납도서목록 조회",notes = "사용자정보(id) -> 반납도서목록 조회")
    @GetMapping("/RentalCard/{userId}/returnbook")
    public ResponseEntity<List<ReturnItemOutputDTO>> getAllReturnItem(@PathVariable String userId) {
        Optional<List<ReturnItemOutputDTO>> allReturnItem = inquiryUsecase.getAllReturnItem(new UserInputDTO(userId, ""));
        return ResponseEntity.status(HttpStatus.OK).body(allReturnItem.get());
    }

    @ApiOperation(value = "대여처리", notes = "사용자정보, 아이템정보1 -> 도시카드정보")
    @PostMapping("/RentalCard/rent")
    public ResponseEntity<RentalCardOutputDTO> rentItem(@RequestBody UserItemInputDTO userItemInputDTO) throws Exception {
        RentalCardOutputDTO rentalCardOutputDTO = rentItemUsecase.rentItem(userItemInputDTO);
        return ResponseEntity.ok(rentalCardOutputDTO);
    }

    @ApiOperation(value = "반납처리", notes = "사용자정보, 아이템정보1 -> 도시카드정보")
    @PostMapping("/RentalCard/return")
    public ResponseEntity<RentalCardOutputDTO> returnItem(@RequestBody UserItemInputDTO userItemInputDTO) throws Exception {
        RentalCardOutputDTO rentalCardOutputDTO = returnItemUsecase.returnItem(userItemInputDTO);
        return ResponseEntity.ok(rentalCardOutputDTO);
    }

    @ApiOperation(value = "연체처리", notes = "사용자정보, 아이템정보1 -> 도시카드정보")
    @PostMapping("/RentalCard/overdue")
    public ResponseEntity<RentalCardOutputDTO> overdueItem(@RequestBody UserItemInputDTO userItemInputDTO) throws Exception {
        RentalCardOutputDTO rentalCardOutputDTO = overdueItemUsecase.overDueItem(userItemInputDTO);
        return ResponseEntity.ok(rentalCardOutputDTO);
    }

    @ApiOperation(value = "연체해제처리", notes = "사용자정보, 포인트 -> 도시카드정보")
    @PostMapping("/RentalCard/clearoverdue")
    public ResponseEntity<RentalResultOutputDTO> clearOverdueItem(@RequestBody ClearOverdueInfoDTO clearOverdueInfoDTO) throws Exception {
        RentalResultOutputDTO rentalResultOutputDTO = clearOverdueItemUsecase.clearOverdue(clearOverdueInfoDTO);
        return ResponseEntity.ok(rentalResultOutputDTO);
    }
}
