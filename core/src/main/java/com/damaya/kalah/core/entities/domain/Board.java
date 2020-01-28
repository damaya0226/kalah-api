package com.damaya.kalah.core.entities.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Board {
    private int[] pits;
}
