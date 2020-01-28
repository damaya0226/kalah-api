package com.damaya.kalah.datastore.memory;

import com.damaya.kalah.core.entities.domain.Game;
import com.damaya.kalah.core.interfaces.GameStorage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryGameStorage implements GameStorage {

    private Map<Long, Game> gameMap;

    public InMemoryGameStorage() {
        this.gameMap = new HashMap<>();
    }

    @Override
    public Game save(Game game) {
        synchronized (gameMap){
            game.setId((long) gameMap.size());
            gameMap.put(game.getId(), game);
        }
        return game;
    }
}
