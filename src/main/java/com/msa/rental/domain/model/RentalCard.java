package com.msa.rental.domain.model;

import com.msa.rental.domain.model.event.ItemRented;
import com.msa.rental.domain.model.event.ItemReturned;
import com.msa.rental.domain.model.event.OverdueCleared;
import com.msa.rental.domain.model.vo.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.boot.jaxb.mapping.internal.InheritanceTypeMarshalling;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalCard {
    @EmbeddedId
    private RentalCardNo rentalCardNo;

    @Embedded
    private IDName member;

    private RentStatus rentStatus;

    @Embedded
    private LateFee lateFee;

    @ElementCollection      // 테이블로 매핑됨
    private List<RentalItem> rentalItemList = new ArrayList<>();
    @ElementCollection
    private List<ReturnItem> returnItemList = new ArrayList<>();


    private void addRentalItem(RentalItem rentalItem) {
        this.rentalItemList.add(rentalItem);
    }
    private void removeRentalItem(RentalItem rentalItem) {
        this.rentalItemList.remove(rentalItem);
    }
    private void removeReturnItem(ReturnItem returnItem) {
        this.returnItemList.remove(returnItem);
    }

    private void addReturnItem(ReturnItem returnItem) {
        this.returnItemList.add(returnItem);
    }

    // 대여카드 생성
    public static RentalCard createRentalCard(IDName creator) {
        RentalCard rentalCard = new RentalCard();
        rentalCard.setRentalCardNo(RentalCardNo.createRentalCardNo());
        rentalCard.setMember(creator);
        rentalCard.setRentStatus(RentStatus.RENT_AVAILABLE);
        rentalCard.setLateFee(LateFee.createLateFee());

        return rentalCard;
    }

    // 대여처리
    public RentalCard rentItem(Item item) {
        checkRentalAvailable();
        this.addRentalItem(RentalItem.createRentalItem(item));
        return this;
    }

    // 대여처리 취소(보상 트랜잭션)
    public RentalCard cancelRentItem(Item item) {
        RentalItem rentalItem = this.rentalItemList.stream().filter(i -> i.getItem().equals(item)).findFirst().get();

        // rentalItemList에서 찾아서 삭제처리
        this.removeRentalItem(rentalItem);

        return this;
    }

    private void checkRentalAvailable() {
        if (this.rentStatus == RentStatus.RENT_UNAVAILABLE) {
            throw new IllegalArgumentException("대여불가상태입니다.");
        }
        if (this.rentalItemList.size() > 5) {
            throw new IllegalArgumentException("이미 5권을 대여했습니다.");
        }
    }

    // 반납처리
    public RentalCard returnItem(Item item, LocalDate returnDate) {
        RentalItem rentalItem = this.rentalItemList.stream().filter(i -> i.getItem().equals(item)).findFirst().get();

        calculateLateFee(rentalItem, returnDate);

        this.addReturnItem(ReturnItem.createReturnItem(rentalItem));
        this.removeRentalItem(rentalItem);
        return this;
    }

    // 반납처리 취소 (보상 트랜잭션)
    public RentalCard cancelReturnItem(Item item, long point) {
        ReturnItem returnItem = this.returnItemList.stream().filter(i -> i.getRentalItem().getItem().equals(item)).findFirst().get();

        // returnItem에 있는 rentalItem을 다시 넣어줌
        this.addRentalItem(returnItem.getRentalItem());

        // 기존 returnItemList에서 제거
        this.removeReturnItem(returnItem);

        return this;
    }

    private void calculateLateFee(RentalItem rentalItem, LocalDate returnDate) {
        // 연체인 경우
        if (returnDate.compareTo(rentalItem.getOverdueDate()) > 0) {
            // 하루에 10 포인트씩 연체포인트
            long point = Period.between(rentalItem.getOverdueDate(), returnDate).getDays() * 10L;
            LateFee addPoint = this.lateFee.addPoint(point);
            this.lateFee.setPoint(addPoint.getPoint());
        }
    }

    // 연체처리 (실제는 배치에서 처리하겠지만 임시로 작성)
    public RentalCard overdueItem(Item item) {
        this.rentStatus = RentStatus.RENT_UNAVAILABLE;  // 해당 사용자의 렌탈카드를 대여불가 상태로 변경

        RentalItem rentalItem = this.rentalItemList.stream().filter(i -> i.getItem().equals(item)).findFirst().get();
        rentalItem.setOverdued(true);   // 연체상태
        rentalItem.setOverdueDate(LocalDate.now().minusDays(1));    // 강제로 반납예정일 변경

        return this;
    }

    // 연체해제 (잔여 연체포인트 리턴)
    public long makeAvailableRental(long point) throws Exception {
        // 모든 도서가 반납상태이어야 함
        if (this.rentalItemList.size() != 0) {
            throw new IllegalArgumentException("모든 도서가 반납되어야 연체정지를 해제할 수 있습니다.");
        }
        // 계산을 쉽게하기 위해 == 만 비교
        if (this.lateFee.getPoint() != point) {
            throw new IllegalArgumentException("해당 포인트로 연체를 해제할 수 없습니다.");
        }

        this.setLateFee(lateFee.removePoint(point));
        if (this.lateFee.getPoint() == 0) {
            this.rentStatus = RentStatus.RENT_AVAILABLE;  // 대여가능 상태로 변경
        }

        return this.getLateFee().getPoint();
    }

    // 연체해제 취소 (보상 트랜잭션)
    public long cancelMakeAvailableRental(long point) {
        this.setLateFee(this.lateFee.addPoint(point));

        //this.setRentStatus(RentStatus.RENT_AVAILABLE);
        this.rentStatus = RentStatus.RENT_AVAILABLE;

        return this.lateFee.getPoint();
    }


    public static ItemRented createItemRentedEvent(IDName idName,Item item,long point) {
        return new ItemRented(idName,item,point);
    }
    public static ItemReturned createItemReturnEvent(IDName idName,Item item,long point) {
        return new ItemReturned(idName,item,point);
    }
    public static OverdueCleared createOverdueCleardEvent(IDName idName,long point) {
        return new OverdueCleared(idName,point);
    }


    // sampleCode
    public static RentalCard sample() {
        RentalCard rentalCard = new RentalCard();
        rentalCard.setRentalCardNo(RentalCardNo.createRentalCardNo());
        rentalCard.setMember(IDName.sample());
        rentalCard.setRentStatus(RentStatus.RENT_AVAILABLE);
        //rentalCard.setLateFee(LateFee.sample());
        rentalCard.setLateFee(LateFee.createLateFee());

        return rentalCard;
    }

}
