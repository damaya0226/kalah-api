package com.damaya.kalah.core.usecases;

import com.damaya.kalah.core.entities.Game;
import com.damaya.kalah.core.interfaces.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);

    @Override
    public Game create() {
        LOGGER.debug("Creating new game...");

        Game game = Game.builder()
                .startedAt(new Date()).build();

        //TODO: Save
        game.setId(1L);

        return game;
    }
}
