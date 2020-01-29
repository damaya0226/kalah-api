package com.damaya.kalah.core.usecases;


import com.damaya.kalah.core.entities.domain.Board;
import com.damaya.kalah.core.entities.domain.Game;
import com.damaya.kalah.core.entities.enums.GameTurn;
import com.damaya.kalah.core.entities.exceptions.GameAlreadyFinishedException;
import com.damaya.kalah.core.entities.exceptions.GameNotFoundException;
import com.damaya.kalah.core.entities.exceptions.InvalidMoveException;
import com.damaya.kalah.core.interfaces.GameStorage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Test of class {@link GameServiceImpl}
 * Created by Diego Amaya on 29/01/2020.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GameServiceImplTest.Config.class})
public class GameServiceImplTest {

    @Autowired
    private GameServiceImpl service;

    @MockBean
    private GameStorage storage;

    @Captor
    private ArgumentCaptor<Game> gameCaptor;

    @Configuration
    public static class Config {

        @Bean
        public GameServiceImpl gameService() {
            return new GameServiceImpl();
        }

    }

    @Test
    public void testCreate(){
        //Given
        Game gameFromStorage = Game.builder().build();
        when(storage.save(any())).thenReturn(gameFromStorage);

        //When
        assertThat(service.create()).isEqualTo(gameFromStorage);

        //Then
        verify(storage, times(1)).save(gameCaptor.capture());
        Game createdGame = gameCaptor.getValue();

        assertThat(createdGame).isNotNull().satisfies(game -> {
            assertThat(game.getId()).isNotEmpty();
            assertThat(game.getStartedAt()).isCloseTo(new Date(), 500L);
            assertThat(game.getFinishedAt()).isNull();
            assertThat(game.getTurn()).isEqualTo(GameTurn.PLAYER_ONE);
            assertThat(game.getBoard()).isNotNull().satisfies(board -> {
                assertThat(board.getPits()).isNotNull().hasSize(14);
            });
        });

    }

    @Test
    public void testMakeMoveShouldThrowGameNotFound(){
        //Given
        String gameId = UUID.randomUUID().toString();
        int pitId = 5;
        when(storage.findById(gameId)).thenReturn(Optional.empty());

        //When and Then
        assertThatExceptionOfType(GameNotFoundException.class)
                .isThrownBy(() -> service.makeMove(gameId, pitId))
                .matches(e -> e.getMessage().equals("Game not found"));

        verify(storage, times(1)).findById(gameId);
        verify(storage, times(0)).save(any());

    }


    @Test
    public void testMakeMoveShouldSuccessAndSwitchPlayer() throws GameAlreadyFinishedException, GameNotFoundException, InvalidMoveException {
        //Given
        Game game = createGame();
        int pitId = 6;
        when(storage.findById(game.getId())).thenReturn(Optional.of(game));

        //When and Then
        service.makeMove(game.getId(), pitId);

        verify(storage, times(1)).findById(game.getId());
        verify(storage, times(1)).save(gameCaptor.capture());

        Game gameAfterMove = gameCaptor.getValue();

        assertThat(gameAfterMove).isNotNull();
        assertThat(gameAfterMove.getFinishedAt()).isNull();
        assertThat(gameAfterMove.getTurn()).isEqualTo(GameTurn.PLAYER_TWO);

        int[] pits = gameAfterMove.getBoard().getPits();

        assertThat(pits[0]).isEqualTo(6);
        assertThat(pits[1]).isEqualTo(6);
        assertThat(pits[2]).isEqualTo(6);
        assertThat(pits[3]).isEqualTo(6);
        assertThat(pits[4]).isEqualTo(6);
        assertThat(pits[5]).isEqualTo(0);
        assertThat(pits[6]).isEqualTo(1);
        assertThat(pits[7]).isEqualTo(7);
        assertThat(pits[8]).isEqualTo(7);
        assertThat(pits[9]).isEqualTo(7);
        assertThat(pits[10]).isEqualTo(7);
        assertThat(pits[11]).isEqualTo(7);
        assertThat(pits[12]).isEqualTo(6);
        assertThat(pits[13]).isEqualTo(0);
    }

    @Test
    public void testMakeMoveShouldSuccessGameAndFinishIt() throws GameAlreadyFinishedException, GameNotFoundException, InvalidMoveException {
        //Given
        Game game = createGame();
        int[] pits = game.getBoard().getPits();
        pits[0] = pits[1] = pits[2] = pits[3] = pits[4] = 0;
        pits[5] = 1;
        int pitId = 6;
        when(storage.findById(game.getId())).thenReturn(Optional.of(game));

        //When and Then
        service.makeMove(game.getId(), pitId);

        verify(storage, times(1)).findById(game.getId());
        verify(storage, times(1)).save(gameCaptor.capture());

        Game gameAfterMove = gameCaptor.getValue();

        assertThat(gameAfterMove).isNotNull();
        assertThat(gameAfterMove.getFinishedAt()).isNotNull();
        assertThat(gameAfterMove.getTurn()).isEqualTo(GameTurn.PLAYER_ONE);
        assertThat(gameAfterMove.getWinner()).isEqualTo(GameTurn.PLAYER_TWO);

        int[] pitsAfterMove = gameAfterMove.getBoard().getPits();

        assertThat(pitsAfterMove[0]).isEqualTo(0);
        assertThat(pitsAfterMove[1]).isEqualTo(0);
        assertThat(pitsAfterMove[2]).isEqualTo(0);
        assertThat(pitsAfterMove[3]).isEqualTo(0);
        assertThat(pitsAfterMove[4]).isEqualTo(0);
        assertThat(pitsAfterMove[5]).isEqualTo(0);
        assertThat(pitsAfterMove[6]).isEqualTo(1);
        assertThat(pitsAfterMove[7]).isEqualTo(6);
        assertThat(pitsAfterMove[8]).isEqualTo(6);
        assertThat(pitsAfterMove[9]).isEqualTo(6);
        assertThat(pitsAfterMove[10]).isEqualTo(6);
        assertThat(pitsAfterMove[11]).isEqualTo(6);
        assertThat(pitsAfterMove[12]).isEqualTo(6);
        assertThat(pitsAfterMove[13]).isEqualTo(0);
    }

    @Test
    public void testMakeMoveShouldNotSwitchPlayerOne() throws GameAlreadyFinishedException, GameNotFoundException, InvalidMoveException {
        //Given
        Game game = createGame();
        int pitId = 6;
        when(storage.findById(game.getId())).thenReturn(Optional.of(game));
        game.getBoard().getPits()[5] = 1;
        //When and Then
        service.makeMove(game.getId(), pitId);

        verify(storage, times(1)).findById(game.getId());
        verify(storage, times(1)).save(gameCaptor.capture());

        Game gameAfterMove = gameCaptor.getValue();

        assertThat(gameAfterMove).isNotNull();
        assertThat(gameAfterMove.getFinishedAt()).isNull();
        assertThat(gameAfterMove.getTurn()).isEqualTo(GameTurn.PLAYER_ONE);

        int[] pits = gameAfterMove.getBoard().getPits();

        assertThat(pits[0]).isEqualTo(6);
        assertThat(pits[1]).isEqualTo(6);
        assertThat(pits[2]).isEqualTo(6);
        assertThat(pits[3]).isEqualTo(6);
        assertThat(pits[4]).isEqualTo(6);
        assertThat(pits[5]).isEqualTo(0);
        assertThat(pits[6]).isEqualTo(1);
        assertThat(pits[7]).isEqualTo(6);
        assertThat(pits[8]).isEqualTo(6);
        assertThat(pits[9]).isEqualTo(6);
        assertThat(pits[10]).isEqualTo(6);
        assertThat(pits[11]).isEqualTo(6);
        assertThat(pits[12]).isEqualTo(6);
        assertThat(pits[13]).isEqualTo(0);
    }

    @Test
    public void testMakeMoveShouldNotSwitchPlayerTwo() throws GameAlreadyFinishedException, GameNotFoundException, InvalidMoveException {
        //Given
        Game game = createGame();
        game.setTurn(GameTurn.PLAYER_TWO);
        int pitId = 13;
        when(storage.findById(game.getId())).thenReturn(Optional.of(game));
        game.getBoard().getPits()[12] = 1;
        //When and Then
        service.makeMove(game.getId(), pitId);

        verify(storage, times(1)).findById(game.getId());
        verify(storage, times(1)).save(gameCaptor.capture());

        Game gameAfterMove = gameCaptor.getValue();

        assertThat(gameAfterMove).isNotNull();
        assertThat(gameAfterMove.getFinishedAt()).isNull();
        assertThat(gameAfterMove.getTurn()).isEqualTo(GameTurn.PLAYER_TWO);

        int[] pits = gameAfterMove.getBoard().getPits();

        assertThat(pits[0]).isEqualTo(6);
        assertThat(pits[1]).isEqualTo(6);
        assertThat(pits[2]).isEqualTo(6);
        assertThat(pits[3]).isEqualTo(6);
        assertThat(pits[4]).isEqualTo(6);
        assertThat(pits[5]).isEqualTo(6);
        assertThat(pits[6]).isEqualTo(0);
        assertThat(pits[7]).isEqualTo(6);
        assertThat(pits[8]).isEqualTo(6);
        assertThat(pits[9]).isEqualTo(6);
        assertThat(pits[10]).isEqualTo(6);
        assertThat(pits[11]).isEqualTo(6);
        assertThat(pits[12]).isEqualTo(0);
        assertThat(pits[13]).isEqualTo(1);
    }


    private Game createGame(){
        return Game.builder()
                .id(UUID.randomUUID().toString())
                .turn(GameTurn.PLAYER_ONE)
                .startedAt(new Date())
                .board(Board.builder()
                        .pits(GameUtils.dealGame(6))
                        .build())
                .build();
    }

}