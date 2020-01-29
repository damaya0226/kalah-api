package com.damaya.kalah.core.usecases;

import com.damaya.kalah.core.entities.domain.Board;
import com.damaya.kalah.core.entities.domain.Game;
import com.damaya.kalah.core.entities.enums.GameTurn;
import com.damaya.kalah.core.entities.exceptions.GameAlreadyFinishedException;
import com.damaya.kalah.core.entities.exceptions.InvalidMoveException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Test of class {@link GameUtils}
 * Created by Diego Amaya on 29/01/2020.
 */
@RunWith(SpringRunner.class)
public class GameUtilsTest {


    @Test
    public void testValidateMoveShouldThrowGameAlreadyFinished() {
        //Given
        Game game = createGame().withFinishedAt(new Date());
        int pitId = 5;
        //When and Then
        assertThatExceptionOfType(GameAlreadyFinishedException.class)
                .isThrownBy(() -> GameUtils.validateMove(game, pitId))
                .matches(e -> e.getMessage().equals("Game is already finished"));
    }

    @Test
    public void testValidateMoveShouldThrowInvalidMovePitIdLessThanOne() {
        //Given
        Game game = createGame();
        int pitId = 0;
        //When and Then
        assertThatExceptionOfType(InvalidMoveException.class)
                .isThrownBy(() -> GameUtils.validateMove(game, pitId))
                .matches(e -> e.getMessage().equals("PitId should be between 1 and 14"));
    }

    @Test
    public void testValidateMoveShouldThrowInvalidMovePitIdGreaterThan14() {
        //Given
        Game game = createGame();
        int pitId = 15;
        //When and Then
        assertThatExceptionOfType(InvalidMoveException.class)
                .isThrownBy(() -> GameUtils.validateMove(game, pitId))
                .matches(e -> e.getMessage().equals("PitId should be between 1 and 14"));
    }

    @Test
    public void testValidateMoveShouldThrowInvalidMoveInvalidWrongTurnPlayerOne() {
        //Given
        Game game = createGame();
        game.setTurn(GameTurn.PLAYER_ONE);
        int pitId = 11;
        //When and Then
        assertThatExceptionOfType(InvalidMoveException.class)
                .isThrownBy(() -> GameUtils.validateMove(game, pitId))
                .matches(e -> e.getMessage().equals("Player is trying to move stone from adversary pit"));
    }

    @Test
    public void testValidateMoveShouldThrowInvalidMoveInvalidWrongTurnPlayerTwo() {
        //Given
        Game game = createGame();
        game.setTurn(GameTurn.PLAYER_TWO);
        int pitId = 5;
        //When and Then
        assertThatExceptionOfType(InvalidMoveException.class)
                .isThrownBy(() -> GameUtils.validateMove(game, pitId))
                .matches(e -> e.getMessage().equals("Player is trying to move stone from adversary pit"));
    }

    @Test
    public void testValidateMoveShouldThrowInvalidMoveMovingHomeStonesPlayerOne() {
        //Given
        Game game = createGame();
        game.setTurn(GameTurn.PLAYER_ONE);
        int pitId = 7;
        //When and Then
        assertThatExceptionOfType(InvalidMoveException.class)
                .isThrownBy(() -> GameUtils.validateMove(game, pitId))
                .matches(e -> e.getMessage().equals("Stones from the house cannot be moved"));
    }

    @Test
    public void testValidateMoveShouldThrowInvalidMoveMovingHomeStonesPlayerTwo() {
        //Given
        Game game = createGame();
        game.setTurn(GameTurn.PLAYER_TWO);
        int pitId = 14;
        //When and Then
        assertThatExceptionOfType(InvalidMoveException.class)
                .isThrownBy(() -> GameUtils.validateMove(game, pitId))
                .matches(e -> e.getMessage().equals("Stones from the house cannot be moved"));
    }


    @Test
    public void testValidateMoveShouldThrowInvalidMoveEmptyPit() {
        //Given
        int pitId = 5;
        Game game = createGame();
        game.getBoard().getPits()[pitId -1] = 0;
        //When and Then
        assertThatExceptionOfType(InvalidMoveException.class)
                .isThrownBy(() -> GameUtils.validateMove(game, pitId))
                .matches(e -> e.getMessage().equals("There are no stones in the pit"));
    }

    @Test
    public void testValidateMoveShouldSuccess() throws InvalidMoveException, GameAlreadyFinishedException {
        //Given
        int pitId = 5;
        Game game = createGame();
        //When and Then
        GameUtils.validateMove(game, pitId);
    }

    @Test
    public void testDealGameShouldThrowIllegalArgumentException(){
        //Given
        int stonesPerPit = 0;
        //When and Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GameUtils.dealGame(stonesPerPit))
                .matches(e -> e.getMessage().equals("Stones per pit should be greater than 0"));
    }

    @Test
    public void testDealGameShouldSuccess(){
        //Given
        int stonesPerPit = 6;
        //When and Then
        int[] pits = GameUtils.dealGame(stonesPerPit);
        assertThat(pits).isNotNull();
        assertThat(pits.length).isEqualTo(14);
        assertThat(pits[6]).isEqualTo(0);
        assertThat(pits[13]).isEqualTo(0);
        IntStream.range(0, 6).forEach(index -> assertThat(pits[index]).isEqualTo(stonesPerPit));
        IntStream.range(7, 13).forEach(index -> assertThat(pits[index]).isEqualTo(stonesPerPit));

    }

    @Test
    public void testIsFinishedWithNullBoard(){
        //Given
        Board board = null;
        //When and Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GameUtils.isFinished(board))
                .matches(e -> e.getMessage().equals("Board must not be null, and should have 14 pits"));
    }

    @Test
    public void testIsFinishedShouldThrowIllegalArgumentWithNullBoardPits(){
        //Given
        Board board = Board.builder().pits(null).build();
        //When and Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GameUtils.isFinished(board))
                .matches(e -> e.getMessage().equals("Board must not be null, and should have 14 pits"));
    }

    @Test
    public void testIsFinishedShouldThrowIllegalArgumentWithBoardPitsNotRightSize(){
        //Given
        Board board = Board.builder().pits(new int[4]).build();
        //When and Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GameUtils.isFinished(board))
                .matches(e -> e.getMessage().equals("Board must not be null, and should have 14 pits"));
    }

    @Test
    public void testIsFinishedWhenGameStillRunning(){
        //Given
        Board board = Board.builder().pits(GameUtils.dealGame(1)).build();

        //When and Then
        assertThat(GameUtils.isFinished(board)).isFalse();
    }

    @Test
    public void testIsFinishedWhenPlayerOneFinishesGame(){
        //Given
        int stonesPerPit = 1;
        int[] pits = GameUtils.dealGame(stonesPerPit);
        pits[0] = pits[1] = pits[2] = pits[3] = pits[4] = pits[5] =0;
        pits[6] = stonesPerPit * 6;
        Board board = Board.builder().pits(pits).build();
        //When and Then
        assertThat(GameUtils.isFinished(board)).isTrue();
    }

    @Test
    public void testIsFinishedWhenPlayerTwoFinishesGame(){
        //Given
        int stonesPerPit = 1;
        int[] pits = GameUtils.dealGame(stonesPerPit);
        pits[7] = pits[8] = pits[9] = pits[10] = pits[11] = pits[12] =0;
        pits[13] = stonesPerPit * 6;
        Board board = Board.builder().pits(pits).build();
        //When and Then
        assertThat(GameUtils.isFinished(board)).isTrue();
    }

    @Test
    public void testGetWinnerPlayerOne(){
        //Given
        int stonesPerPit = 1;
        int[] pits = GameUtils.dealGame(stonesPerPit);
        pits[7] = pits[8] = pits[9] = pits[10] = pits[11] = pits[12] =0;
        pits[13] = 1;
        Board board = Board.builder().pits(pits).build();
        //When and Then
        assertThat(GameUtils.getWinner(board)).isEqualTo(GameTurn.PLAYER_ONE);
    }

    @Test
    public void testGetWinnerPlayerTwo(){
        //Given
        int stonesPerPit = 1;
        int[] pits = GameUtils.dealGame(stonesPerPit);
        pits[0] = pits[1] = pits[2] = pits[3] = pits[4] = pits[5] =0;
        pits[6] = 1;
        Board board = Board.builder().pits(pits).build();
        //When and Then
        assertThat(GameUtils.getWinner(board)).isEqualTo(GameTurn.PLAYER_TWO);
    }

    @Test
    public void testGetWinnerWhenTheGameHasNotFinished(){
        //Given
        Board board = Board.builder().pits(GameUtils.dealGame(1)).build();

        //When and Then
        assertThat(GameUtils.getWinner(board)).isNull();
    }

    @Test
    public void testDistributeStonesShouldThrowIllegalArgumentWithNullPits(){
        //Given
        int[] pits = null;
        int index = 1;
        GameTurn turn = GameTurn.PLAYER_ONE;

        //When and Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GameUtils.distributeStones(pits, index, turn))
                .matches(e -> e.getMessage().equals("Pits and turn must not be null, and should have 14 pits"));
    }

    @Test
    public void testDistributeStonesShouldThrowIllegalArgumentWithInvalidPitsLength(){
        //Given
        int[] pits = new int[4];
        int index = 1;
        GameTurn turn = GameTurn.PLAYER_ONE;

        //When and Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GameUtils.distributeStones(pits, index, turn))
                .matches(e -> e.getMessage().equals("Pits and turn must not be null, and should have 14 pits"));
    }

    @Test
    public void testDistributeStonesShouldThrowIllegalArgumentWithNullTurn(){
        //Given
        int[] pits = new int[14];
        int index = 1;
        GameTurn turn = null;

        //When and Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GameUtils.distributeStones(pits, index, turn))
                .matches(e -> e.getMessage().equals("Pits and turn must not be null, and should have 14 pits"));
    }

    @Test
    public void testDistributeStonesShouldThrowIllegalArgumentWithIndexLessThanZero(){
        //Given
        int[] pits = new int[14];
        int index = -1;
        GameTurn turn = GameTurn.PLAYER_ONE;

        //When and Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GameUtils.distributeStones(pits, index, turn))
                .matches(e -> e.getMessage().equals("Pits and turn must not be null, and should have 14 pits"));
    }

    @Test
    public void testDistributeStonesShouldThrowIllegalArgumentWithIndexGreaterThan13(){
        //Given
        int[] pits = new int[14];
        int index = 14;
        GameTurn turn = GameTurn.PLAYER_ONE;

        //When and Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GameUtils.distributeStones(pits, index, turn))
                .matches(e -> e.getMessage().equals("Pits and turn must not be null, and should have 14 pits"));
    }

    @Test
    public void testDistributeStonesGame1(){
        //Given
        int pitId = 6;
        int[] pits = GameUtils.dealGame(6);
        int index = pitId - 1;
        GameTurn turn = GameTurn.PLAYER_ONE;

        //When and Then
        int lastPitIndex = GameUtils.distributeStones(pits, index, turn);
        assertThat(lastPitIndex).isEqualTo(11);

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
    public void testDistributeStonesGame2(){
        //Given
        int pitId = 6;
        int[] pits = GameUtils.dealGame(6);
        int index = pitId - 1;
        GameTurn turn = GameTurn.PLAYER_ONE;
        GameUtils.distributeStones(pits, index, turn);

        //When
        turn = GameTurn.PLAYER_TWO;
        pitId = 12;
        index = pitId - 1;
        int lastPitIndex = GameUtils.distributeStones(pits, index, turn);

        //Then
        assertThat(lastPitIndex).isEqualTo(4);

        assertThat(pits[0]).isEqualTo(7);
        assertThat(pits[1]).isEqualTo(7);
        assertThat(pits[2]).isEqualTo(7);
        assertThat(pits[3]).isEqualTo(7);
        assertThat(pits[4]).isEqualTo(7);
        assertThat(pits[5]).isEqualTo(0);
        assertThat(pits[6]).isEqualTo(1);
        assertThat(pits[7]).isEqualTo(7);
        assertThat(pits[8]).isEqualTo(7);
        assertThat(pits[9]).isEqualTo(7);
        assertThat(pits[10]).isEqualTo(7);
        assertThat(pits[11]).isEqualTo(0);
        assertThat(pits[12]).isEqualTo(7);
        assertThat(pits[13]).isEqualTo(1);
    }


    @Test
    public void testDistributeStonesGame3(){
        //Given
        int pitId = 6;
        int[] pits = GameUtils.dealGame(6);
        int index = pitId - 1;
        GameTurn turn = GameTurn.PLAYER_ONE;
        //Move 1
        GameUtils.distributeStones(pits, index, turn);
        //Move 2
        turn = GameTurn.PLAYER_TWO;
        pitId = 12;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);

        //When
        turn = GameTurn.PLAYER_ONE;
        pitId = 5;
        index = pitId - 1;
        int lastPitIndex = GameUtils.distributeStones(pits, index, turn);

        //Then
        assertThat(lastPitIndex).isEqualTo(11);

        assertThat(pits[0]).isEqualTo(7);
        assertThat(pits[1]).isEqualTo(7);
        assertThat(pits[2]).isEqualTo(7);
        assertThat(pits[3]).isEqualTo(7);
        assertThat(pits[4]).isEqualTo(0);
        assertThat(pits[5]).isEqualTo(1);
        assertThat(pits[6]).isEqualTo(2);
        assertThat(pits[7]).isEqualTo(8);
        assertThat(pits[8]).isEqualTo(8);
        assertThat(pits[9]).isEqualTo(8);
        assertThat(pits[10]).isEqualTo(8);
        assertThat(pits[11]).isEqualTo(1);
        assertThat(pits[12]).isEqualTo(7);
        assertThat(pits[13]).isEqualTo(1);
    }


    @Test
    public void testDistributeStonesGame4(){
        //Given
        int pitId = 6;
        int[] pits = GameUtils.dealGame(6);
        int index = pitId - 1;
        GameTurn turn = GameTurn.PLAYER_ONE;
        //Move 1
        GameUtils.distributeStones(pits, index, turn);
        //Move 2
        turn = GameTurn.PLAYER_TWO;
        pitId = 12;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 3
        turn = GameTurn.PLAYER_ONE;
        pitId = 5;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);

        //When
        turn = GameTurn.PLAYER_TWO;
        pitId = 13;
        index = pitId - 1;
        int lastPitIndex = GameUtils.distributeStones(pits, index, turn);

        //Then
        assertThat(lastPitIndex).isEqualTo(5);

        assertThat(pits[0]).isEqualTo(8);
        assertThat(pits[1]).isEqualTo(8);
        assertThat(pits[2]).isEqualTo(8);
        assertThat(pits[3]).isEqualTo(8);
        assertThat(pits[4]).isEqualTo(1);
        assertThat(pits[5]).isEqualTo(2);
        assertThat(pits[6]).isEqualTo(2);
        assertThat(pits[7]).isEqualTo(8);
        assertThat(pits[8]).isEqualTo(8);
        assertThat(pits[9]).isEqualTo(8);
        assertThat(pits[10]).isEqualTo(8);
        assertThat(pits[11]).isEqualTo(1);
        assertThat(pits[12]).isEqualTo(0);
        assertThat(pits[13]).isEqualTo(2);
    }

    @Test
    public void testDistributeStonesGame6(){
        //Given
        int pitId = 6;
        int[] pits = GameUtils.dealGame(6);
        int index = pitId - 1;
        GameTurn turn = GameTurn.PLAYER_ONE;
        //Move 1
        GameUtils.distributeStones(pits, index, turn);
        //Move 2
        turn = GameTurn.PLAYER_TWO;
        pitId = 12;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 3
        turn = GameTurn.PLAYER_ONE;
        pitId = 5;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 4
        turn = GameTurn.PLAYER_TWO;
        pitId = 13;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 5
        turn = GameTurn.PLAYER_ONE;
        pitId = 3;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);

        //When
        turn = GameTurn.PLAYER_TWO;
        pitId = 12;
        index = pitId - 1;
        int lastPitIndex = GameUtils.distributeStones(pits, index, turn);

        //Then
        assertThat(lastPitIndex).isEqualTo(12);

        assertThat(pits[0]).isEqualTo(0);
        assertThat(pits[1]).isEqualTo(8);
        assertThat(pits[2]).isEqualTo(0);
        assertThat(pits[3]).isEqualTo(9);
        assertThat(pits[4]).isEqualTo(2);
        assertThat(pits[5]).isEqualTo(3);
        assertThat(pits[6]).isEqualTo(3);
        assertThat(pits[7]).isEqualTo(9);
        assertThat(pits[8]).isEqualTo(9);
        assertThat(pits[9]).isEqualTo(9);
        assertThat(pits[10]).isEqualTo(9);
        assertThat(pits[11]).isEqualTo(0);
        assertThat(pits[12]).isEqualTo(0);
        assertThat(pits[13]).isEqualTo(11);
    }

    @Test
    public void testDistributeStonesGame7(){
        //Given
        int pitId = 6;
        int[] pits = GameUtils.dealGame(6);
        int index = pitId - 1;
        GameTurn turn = GameTurn.PLAYER_ONE;
        //Move 1
        GameUtils.distributeStones(pits, index, turn);
        //Move 2
        turn = GameTurn.PLAYER_TWO;
        pitId = 12;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 3
        turn = GameTurn.PLAYER_ONE;
        pitId = 5;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 4
        turn = GameTurn.PLAYER_TWO;
        pitId = 13;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 5
        turn = GameTurn.PLAYER_ONE;
        pitId = 3;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 6
        turn = GameTurn.PLAYER_TWO;
        pitId = 12;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);

        //When
        turn = GameTurn.PLAYER_ONE;
        pitId = 5;
        index = pitId - 1;
        int lastPitIndex = GameUtils.distributeStones(pits, index, turn);

        //Then
        assertThat(lastPitIndex).isEqualTo(6);

        assertThat(pits[0]).isEqualTo(0);
        assertThat(pits[1]).isEqualTo(8);
        assertThat(pits[2]).isEqualTo(0);
        assertThat(pits[3]).isEqualTo(9);
        assertThat(pits[4]).isEqualTo(0);
        assertThat(pits[5]).isEqualTo(4);
        assertThat(pits[6]).isEqualTo(4);
        assertThat(pits[7]).isEqualTo(9);
        assertThat(pits[8]).isEqualTo(9);
        assertThat(pits[9]).isEqualTo(9);
        assertThat(pits[10]).isEqualTo(9);
        assertThat(pits[11]).isEqualTo(0);
        assertThat(pits[12]).isEqualTo(0);
        assertThat(pits[13]).isEqualTo(11);
    }

    @Test
    public void testDistributeStonesGame9(){
        //Given
        int pitId = 6;
        int[] pits = GameUtils.dealGame(6);
        int index = pitId - 1;
        GameTurn turn = GameTurn.PLAYER_ONE;
        //Move 1
        GameUtils.distributeStones(pits, index, turn);
        //Move 2
        turn = GameTurn.PLAYER_TWO;
        pitId = 12;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 3
        turn = GameTurn.PLAYER_ONE;
        pitId = 5;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 4
        turn = GameTurn.PLAYER_TWO;
        pitId = 13;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 5
        turn = GameTurn.PLAYER_ONE;
        pitId = 3;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 6
        turn = GameTurn.PLAYER_TWO;
        pitId = 12;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 7
        turn = GameTurn.PLAYER_ONE;
        pitId = 5;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);
        //Move 8
        turn = GameTurn.PLAYER_ONE;
        pitId = 4;
        index = pitId - 1;
        GameUtils.distributeStones(pits, index, turn);

        //When
        turn = GameTurn.PLAYER_TWO;
        pitId = 11;
        index = pitId - 1;
        int lastPitIndex = GameUtils.distributeStones(pits, index, turn);

        //Then
        assertThat(lastPitIndex).isEqualTo(7);

        assertThat(pits[0]).isEqualTo(1);
        assertThat(pits[1]).isEqualTo(9);
        assertThat(pits[2]).isEqualTo(1);
        assertThat(pits[3]).isEqualTo(1);
        assertThat(pits[4]).isEqualTo(2);
        assertThat(pits[5]).isEqualTo(6);
        assertThat(pits[6]).isEqualTo(5);
        assertThat(pits[7]).isEqualTo(11);
        assertThat(pits[8]).isEqualTo(10);
        assertThat(pits[9]).isEqualTo(10);
        assertThat(pits[10]).isEqualTo(0);
        assertThat(pits[11]).isEqualTo(2);
        assertThat(pits[12]).isEqualTo(2);
        assertThat(pits[13]).isEqualTo(12);
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