package com.damaya.kalah.mappers;

import com.damaya.kalah.controllers.GameController;
import com.damaya.kalah.core.entities.domain.Board;
import com.damaya.kalah.core.entities.domain.Game;
import com.damaya.kalah.dtos.GameResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class GameResponseMapper {

    private GameResponseMapper(){}

    public static GameResponse map(Game game){
        return GameResponse.builder()
                .id(game.getId())
                .status(mapPits(game.getBoard()))
                .url(linkTo(GameController.class).slash(game.getId()).withSelfRel().getHref())
                .build();
    }

    private static Map<String, String> mapPits(Board board) {
        Map<String, String> status = null;
        if(Objects.nonNull(board) && Objects.nonNull(board.getPits())){
            status = new HashMap<>();
            for(int i = 1; i <= board.getPits().length; i++){
                status.put(String.valueOf(i), String.valueOf(board.getPits()[i-1]));
            }
        }
        return status;
    }
}
