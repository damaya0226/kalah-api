package com.damaya.kalah.core.usecases;

import com.damaya.kalah.core.entities.domain.Board;
import com.damaya.kalah.core.entities.domain.Game;
import com.damaya.kalah.core.interfaces.GameService;
import com.damaya.kalah.core.interfaces.GameStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GameServiceImpl implements GameService {

    private static final int NUMBER_OF_STONES = 6;

    @Autowired
    private GameStorage storage;

    @Override
    public Game create() {
        return storage.save(Game.builder()
                .startedAt(new Date())
                .board(Board.builder()
                        .pits(dealGame(NUMBER_OF_STONES))
                        .build())
                .build());
    }

    private int[] dealGame(int stonesPerPit){
        int pits[] = new int[14];
        for(int i = 0 ; i < pits.length ; i++){
            pits[i] = stonesPerPit;
        }
        //Starts Home
        pits[0] = 0;
        pits[6] = 0;
        return pits;
    }
}
