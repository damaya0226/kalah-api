package com.damaya.kalah.controllers;

import com.damaya.kalah.core.entities.exceptions.GameAlreadyFinishedException;
import com.damaya.kalah.core.entities.exceptions.GameNotFoundException;
import com.damaya.kalah.core.entities.exceptions.InvalidMoveException;
import com.damaya.kalah.core.interfaces.GameService;
import com.damaya.kalah.dtos.GameResponse;
import com.damaya.kalah.mappers.GameResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/games", produces = "application/json")
public class GameController {

    private final GameService service;

    @Autowired
    public GameController(GameService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public GameResponse createGame(){
        return GameResponseMapper.map(service.create().withBoard(null));
    }

    @PutMapping("/{gameId}/pits/{pitId}")
    public GameResponse makeMove(@PathVariable String gameId, @PathVariable int pitId) throws GameAlreadyFinishedException, GameNotFoundException
            , InvalidMoveException {
        return GameResponseMapper.map(service.makeMove(gameId, pitId));
    }

}
