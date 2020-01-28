package com.damaya.kalah.core.entities;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Game {

    private Long id;
    private Date startedAt;

}
