package com.damaya.kalah.core.usecases;

import com.damaya.kalah.core.entities.domain.Board;
import com.damaya.kalah.core.entities.domain.Game;
import com.damaya.kalah.core.entities.enums.GameTurn;
import com.damaya.kalah.core.entities.exceptions.GameAlreadyFinishedException;
import com.damaya.kalah.core.entities.exceptions.InvalidMoveException;

import java.util.Objects;

public class GameUtils {

    private static final int TOTAL_PITS = 14;

    private GameUtils(){

    }

    /**
     * Validate if the movement is allowed
     * @param game game
     * @param pitId pit index
     * @throws GameAlreadyFinishedException when the game is already finished
     * @throws InvalidMoveException when the move is invalid
     */
    public static void validateMove(Game game, int pitId) throws GameAlreadyFinishedException, InvalidMoveException {
        if(Objects.isNull(game.getFinishedAt())){
            if(pitId < 1 || pitId > 14){
                throw new InvalidMoveException("PitId should be between 1 and 14");
            }else if(!isPlayerPit(pitId, game.getTurn())){
                throw new InvalidMoveException("Player is trying to move stone from adversary pit");
            }else if(pitId == 7 || pitId == 14){
                throw new InvalidMoveException("Stones from the house cannot be moved");
            }else{
                int[] pits = game.getBoard().getPits();
                if(pits[pitId - 1] == 0){
                    throw new InvalidMoveException("There are no stones in the pit");
                }
            }
        }else {
            throw new GameAlreadyFinishedException("Game is already finished");
        }
    }

    /**
     * Create a new pits board
     * @param stonesPerPit stones in each pit
     * @return pits array
     */
    public static int[] dealGame(int stonesPerPit){
        if(stonesPerPit > 0){
            int pits[] = new int[14];
            for(int i = 0 ; i < pits.length ; i++){
                pits[i] = stonesPerPit;
            }
            //Starts Home
            pits[6] = 0;
            pits[14 - 1] = 0;
            return pits;
        }else{
            throw new IllegalArgumentException("Stones per pit should be greater than 0");
        }
    }

    /**
     * Distributes stones
     * @param pits pits array
     * @param index starting index
     * @param turn current turn
     * @return index of last modified pit
     */
    public static int distributeStones(int[] pits, int index, GameTurn turn) {
        boolean isIndexOk = index >= 0 && index <= TOTAL_PITS - 1;
        if(Objects.nonNull(pits) && Objects.nonNull(turn)
                && pits.length == TOTAL_PITS && isIndexOk){
            int stones = pits[index];
            int stonesToDistribute = stones;
            int lastPitIndex = 0;
            pits[index] = 0;
            for(int i = 0; i < stonesToDistribute; i++){
                lastPitIndex = (index + i + 1) % TOTAL_PITS;
                //Does not put stone if it is adversary home
                if(isAdversaryHome(lastPitIndex, turn)){
                    stonesToDistribute++;
                }else {
                    pits[lastPitIndex] += 1;
                }
            }

            // last pit had 0 stones
            if(isLastMoveEmpty(pits, lastPitIndex)
                    && isPlayerPit(lastPitIndex + 1, turn)
                    && !isLastMoveHome(lastPitIndex, turn)){
                stealStones(pits, lastPitIndex, turn);
            }

            return lastPitIndex;
        }else{
            throw new IllegalArgumentException("Pits and turn must not be null, and should have 14 pits");
        }

    }

    /**
     * Check if a game board is finished, that is, if someone already won
     * @param board game board
     * @return true if the game has finished
     */
    public static boolean isFinished(final Board board) {
        if(Objects.nonNull(board) && Objects.nonNull(board.getPits())
                        && board.getPits().length == TOTAL_PITS){
            int[] pits = board.getPits();
            int sumPlayer1 = 0;
            int sumPlayer2 = 0;
            for(int i = 0; i < 6; i++){
                sumPlayer1 += pits[i];
            }

            for(int i = 7; i < 13; i++){
                sumPlayer2 += pits[i];
            }
            return sumPlayer1 == 0 || sumPlayer2 == 0;
        }else{
            throw new IllegalArgumentException("Board must not be null, and should have 14 pits");
        }
    }

    /**
     * Check if last move was home
     * @param lastMoveIndex last move index
     * @param turn
     * @return true if last move was home
     */
    public static boolean isLastMoveHome(int lastMoveIndex, GameTurn turn){
        return (lastMoveIndex == 6 && turn.equals(GameTurn.PLAYER_ONE))
                || (lastMoveIndex == TOTAL_PITS - 1 && turn.equals(GameTurn.PLAYER_TWO)) ;
    }

    /**
     * Get the game winner if the game is finished
     * @param board game board
     * @return null if the game is not finished or the player who won
     */
    public static GameTurn getWinner(final Board board){
        GameTurn result = null;
        if(isFinished(board)){
            int[] pits = board.getPits();
            int playerOneStones = 0;
            int playerTwoStones = 0;

            for(int i = 0; i < 7; i++){
                playerOneStones += pits[i];
            }

            for(int i = 7; i < TOTAL_PITS; i++){
                playerTwoStones += pits[i];
            }

            if(playerOneStones > playerTwoStones){
                result = GameTurn.PLAYER_ONE;
            }else{
                result = GameTurn.PLAYER_TWO;
            }

        }
        return result;
    }

    private static boolean isAdversaryHome(int indexToPutStone, GameTurn turn) {
        return (turn.equals(GameTurn.PLAYER_TWO) && indexToPutStone == 6)
                || (turn.equals(GameTurn.PLAYER_ONE) && indexToPutStone == 13);
    }

    private static int[] stealStones(int[] pits, int lastMoveIndex, GameTurn status) {
        //add stolen stones to home
        int homeIndex = status.equals(GameTurn.PLAYER_ONE) ? 6 : (TOTAL_PITS - 1);
        int indexToStealFrom = TOTAL_PITS - (lastMoveIndex % TOTAL_PITS) - 2;
        pits[homeIndex] += pits[indexToStealFrom] + 1;
        //set source pits to zero
        pits[lastMoveIndex] = 0;
        pits[indexToStealFrom] = 0;
        return pits;
    }

    private static boolean isLastMoveEmpty(int[] pits, int lastPitIndex) {
        return pits[lastPitIndex] - 1 == 0;
    }

    private static boolean isPlayerPit(int pitId, GameTurn turn){
        return pitId <= 7 && turn.equals(GameTurn.PLAYER_ONE)
                || pitId > 7 && turn.equals(GameTurn.PLAYER_TWO);

    }
}
