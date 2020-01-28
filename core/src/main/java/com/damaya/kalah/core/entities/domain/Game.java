package com.damaya.kalah.core.entities.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Game {

    private Long id;
    private Date startedAt;
    private Board board;

}
