package eu.dorsum.trainees.chessdemo.service;

import eu.dorsum.trainees.chessdemo.model.GameResponse;
import eu.dorsum.trainees.chessdemo.model.Move;
import eu.dorsum.trainees.chessdemo.model.StartState;
import eu.dorsum.trainees.chessdemo.model.board.BitBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Chess {
    private StartStateService startStateService;
    private MoveService moveService;
    private GameService gameService;

    @Autowired
    public Chess(StartStateService startStateService, MoveService moveService){
        this.startStateService = startStateService;
        this.moveService = moveService;
    }

    public void makeMove(long gameID, long playerID, String move){

    }

    public BitBoard getBoardsByGameID(long gameID){
        StartState startState = startStateService.getStartStateById(gameID);
        BitBoard boards = new BitBoard();

        boards.initBoard(startState.getPositions(), startState.getSideToMove());

        return boards;
    }

    public GameResponse GetGameResponse(long gameID){
        GameResponse response = new GameResponse();

        BitBoard boards = recreateBoard(gameID);
        List<Move> moves = moveService.getMovesOfGameAsc(gameID);
        Map<String, Long> history = new HashMap<>();

        for (Move move : moves){
            history.put(move.getNotation(), move.getElapsedTimeMs());
        }

        response.setGame(gameService.getGameById(gameID));
        // response.setBoard(); TODO: bitboard conversion to String not done yet
        // response.setCheck(boards.); TODO: board check handling
        response.setWhiteTurn(boards.isWhiteTurn());
        response.setHistory(history);

        return response;
    }

    public BitBoard recreateBoard(long gameID){
        BitBoard bitBoard = getBoardsByGameID(gameID);
        List<Move> moves = moveService.getMovesOfGameAsc(gameID);

        for (Move m : moves){
            m.getNotation();
        }

        return null;
    }



}