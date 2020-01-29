package com.damaya.kalah.core.entities.domain;

import com.damaya.kalah.core.entities.enums.GameTurn;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.util.Date;

@With
@Data
@Builder
public class Game {

    private String id;
    private Board board;
    private GameTurn turn;

    private Date startedAt;
    private Date finishedAt;
    private GameTurn winner;
}
