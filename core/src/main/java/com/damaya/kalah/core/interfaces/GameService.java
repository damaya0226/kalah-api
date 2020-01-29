package com.damaya.kalah.core.interfaces;

import com.damaya.kalah.core.entities.domain.Game;
import com.damaya.kalah.core.entities.exceptions.GameAlreadyFinishedException;
import com.damaya.kalah.core.entities.exceptions.GameNotFoundException;
import com.damaya.kalah.core.entities.exceptions.InvalidMoveException;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Validated
public interface GameService {

    /**
     * Creates a new Halah game
     * @return game details
     */
    Game create();

    /**
     * Make a move in the board
     * @param gameId id of the game
     * @param pitId pit index
     * @return updated game
     * @throws GameNotFoundException gameId does not match any game
     * @throws GameAlreadyFinishedException game is already
     * @throws InvalidMoveException when the move is forbidden
     */
    Game makeMove(@NotNull String gameId, @Min(1) @Max(14) int pitId) throws GameNotFoundException, GameAlreadyFinishedException, InvalidMoveException;
}
