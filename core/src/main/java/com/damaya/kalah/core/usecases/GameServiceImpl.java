package com.damaya.kalah.core.usecases;

import com.damaya.kalah.core.entities.domain.Board;
import com.damaya.kalah.core.entities.domain.Game;
import com.damaya.kalah.core.entities.enums.GameTurn;
import com.damaya.kalah.core.entities.exceptions.GameAlreadyFinishedException;
import com.damaya.kalah.core.entities.exceptions.GameNotFoundException;
import com.damaya.kalah.core.entities.exceptions.InvalidMoveException;
import com.damaya.kalah.core.interfaces.GameService;
import com.damaya.kalah.core.interfaces.GameStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static com.damaya.kalah.core.usecases.GameUtils.*;

@Service
public class GameServiceImpl implements GameService {

    private static final int NUMBER_OF_STONES = 6;

    @Autowired
    private GameStorage storage;

    @Override
    public Game create() {
        return storage.save(Game.builder()
                .id(UUID.randomUUID().toString())
                .startedAt(new Date())
                .turn(GameTurn.PLAYER_ONE)
                .board(Board.builder()
                        .pits(dealGame(NUMBER_OF_STONES))
                        .build())
                .build());
    }

    @Override
    public Game makeMove(String gameId, int pitId) throws GameNotFoundException, GameAlreadyFinishedException
            , InvalidMoveException {
        Game game = storage.findById(gameId).orElseThrow(() -> new GameNotFoundException("Game not found"));
        GameUtils.validateMove(game, pitId);
        return processMove(game, pitId);
    }

    private Game processMove(final Game game, final int pitId) {
        int[] pits = game.getBoard().getPits();
        int index = pitId - 1;
        GameTurn turn = game.getTurn();

        //Distribute stones
        int lastMoveIndex = distributeStones(pits, index, turn);

        //Update status
        GameTurn winner = getWinner(game.getBoard());
        if(Objects.nonNull(winner)){
            game.setFinishedAt(new Date());
            game.setWinner(winner);
        }else{
            // last move was home? -> player has a new turn
            if(!isLastMoveHome(lastMoveIndex, turn)){
                //change player turn
                game.setTurn(turn.equals(GameTurn.PLAYER_TWO)
                        ? GameTurn.PLAYER_ONE : GameTurn.PLAYER_TWO);
            }
        }

        return storage.save(game);
    }
}
