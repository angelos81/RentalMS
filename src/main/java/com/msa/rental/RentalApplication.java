package com.msa.rental;

import com.msa.rental.domain.model.RentalCard;
import com.msa.rental.domain.model.vo.Item;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.stream.Collectors;

@SpringBootApplication
public class RentalApplication {

    public static void main(String[] args) throws Exception {

        // test
        //domainTest();

        SpringApplication.run(RentalApplication.class, args);
    }

    private static void domainTest() throws Exception {
        System.out.println("✨ ✨ ✨ ✨ ✨ ✨ ✨ ✨ ✨ 도메인 모델 테스트 진행 ✨ ✨ ✨ ✨ ✨ ✨ ✨ ✨ ✨");
        RentalCard sampleCard = RentalCard.sample();
        showCardStatus(sampleCard);

        Item sample1 = new Item(1, "도서1");
        Item sample2 = new Item(2, "도서2");

        rentAndShowStatus(sampleCard, sample1);
        rentAndShowStatus(sampleCard, sample2);
        returnAndShowStatus(sampleCard, sample1);
        overdueAndShowStatus(sampleCard, sample2);
        returnAndShowStatus(sampleCard, sample2);
        makeAvailableAndShowStatus(sampleCard);
    }
    private static void rentAndShowStatus(RentalCard card, Item item) throws Exception {
        card.rentItem(item);
        System.out.println("📗도서명: " + item.getTitle() + " 대여됨");
        showCardStatus(card);
    }
    private static void returnAndShowStatus(RentalCard card, Item item) throws Exception {
        card.returnItem(item, LocalDate.now());
        System.out.println("📗도서명: " + item.getTitle() + " 반납됨");
        showCardStatus(card);
    }
    private static void overdueAndShowStatus(RentalCard card, Item item) {
        card.overdueItem(item);
        System.out.println("📗도서명: " + item.getTitle() + " 강제 연체");
        showCardStatus(card);
    }
    private static void makeAvailableAndShowStatus(RentalCard card) throws Exception {
        long minusPoint = card.makeAvailableRental(card.getLateFee().getPoint());
        System.out.println("🔒🔒 정지해제 처리");
        System.out.println("🔒🔒 현재 남은 연체료는 " + card.getLateFee().getPoint());
        System.out.println("🔒🔒 회원포인트에서 삭감될 포인트는 " + minusPoint);
        showCardStatus(card);
    }
    private static void showCardStatus(RentalCard card) {
        System.out.println("✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️✳️");
        System.out.println("📗📗 " + card.getMember().getName() + " 도서카드 ");
        System.out.println("📗📗 대여도서 연체상태 : " + card.getRentalItemList().stream().map(m -> m.isOverdued()).collect(Collectors.toList()));
        System.out.println("📗📗 총연체료: " + card.getLateFee().getPoint());
        System.out.println("📗📗 대여가능여부: " + card.getRentStatus().toString());
        System.out.println("📗📗 대여 목록");
        System.out.println(" " + card.getRentalItemList().stream().map(m -> m.getItem().getTitle()).collect(Collectors.toList()));
        System.out.println("📗📗 반납목록");
        System.out.println(" " + card.getReturnItemList().stream().map(m -> m.getRentalItem().getItem().getTitle()).collect(Collectors.toList()));
        System.out.println("");
    }

}
