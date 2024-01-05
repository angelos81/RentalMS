package com.msa.rental.domain.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Integer no;
    private String title;


    // sampleCode
    public static Item sample() {
        return new Item(100, "MSA실습");
    }
    public static void main(String[] args) {
        System.out.println(sample().toString());
    }
}