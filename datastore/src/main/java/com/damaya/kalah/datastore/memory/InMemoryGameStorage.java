package com.damaya.kalah.datastore.memory;

import com.damaya.kalah.core.entities.domain.Game;
import com.damaya.kalah.core.interfaces.GameStorage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryGameStorage implements GameStorage {

    private Map<String, Game> gameMap;

    public InMemoryGameStorage() {
        this.gameMap = new HashMap<>();
    }

    @Override
    public Game save(Game game) {
        gameMap.put(game.getId(), game);
        return game;
    }

    @Override
    public Optional<Game> findById(String gameId) {
        return Optional.ofNullable(gameMap.get(gameId));
    }
}
