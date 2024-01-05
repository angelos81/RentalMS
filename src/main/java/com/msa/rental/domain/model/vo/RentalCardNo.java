package com.msa.rental.domain.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalCardNo {
    private String no;

    public static  RentalCardNo createRentalCardNo() {
        UUID uuid = UUID.randomUUID();
        String year = String.valueOf(LocalDate.now().getYear());
        String str = year + '-' + uuid;

        RentalCardNo rentalCardNo = new RentalCardNo();
        rentalCardNo.setNo(str);

        return rentalCardNo;
    }


    // sampleCode
    public static RentalCardNo sample() {
        return RentalCardNo.createRentalCardNo();
    }
    public static void main(String[] args) {
        System.out.println(RentalCardNo.sample());
    }
}
