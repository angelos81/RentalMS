package com.msa.rental.domain.model.event;

import com.msa.rental.domain.model.vo.IDName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OverdueCleared implements Serializable {
    private static final long serialVersionUID = 5138429001414890436L;

    private IDName idName;
    private long point;
}
