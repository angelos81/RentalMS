package com.msa.rental.domain.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class IDName {
    private String id;
    private String name;


    // sampleCode
    public static IDName sample() {
        return new IDName("no1", "hong");
    }
    public static void main(String[] args) {
        System.out.println(sample().toString());
    }
}
