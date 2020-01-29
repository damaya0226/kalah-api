package com.damaya.kalah.core.interfaces;

import com.damaya.kalah.core.entities.domain.Game;

import java.util.Optional;

/**
 * Store all the information related to kalah games
 */
public interface GameStorage {

    /**
     * Create a game in the data storage
     * @param game new game
     * @return created game with new id in case of creation
     */
    Game save(Game game);

    Optional<Game> findById(String gameId);
}
