package com.damaya.kalah.core.interfaces;

import com.damaya.kalah.core.entities.domain.Game;
import org.springframework.validation.annotation.Validated;

@Validated
public interface GameService {

    /**
     * Creates a new Halah game
     * @return game details
     */
    Game create();
}
