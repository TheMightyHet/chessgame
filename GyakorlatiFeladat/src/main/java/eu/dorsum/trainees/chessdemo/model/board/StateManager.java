package eu.dorsum.trainees.chessdemo.model.board;

import java.util.HashMap;
import java.util.Map;

public class StateManager implements Board {

    public StateManager(){}

    public long getPiecesOfType(String state, boolean isWhite, int pieceType){

        Map<Character, Integer> pieces = new HashMap<>();

        pieces.put('P', 0);
        pieces.put('B', 1);
        pieces.put('N', 2);
        pieces.put('R', 3);
        pieces.put('Q', 4);
        pieces.put('K', 5);

        long boardToBuild = 0x0000000000000000;
        String[] squares = state.split("/");

        for (int i = 0; i < squares.length; ++i){

            if(squares[i].isEmpty()) {continue;}
            if(squares[i].charAt(0) == 'W' && isWhite && pieces.get(squares[i].charAt(1)) == pieceType ||
            squares[i].charAt(0) == 'B' && !isWhite && pieces.get(squares[i].charAt(1)) == pieceType) {

                boardToBuild |= (1L << i);
            }
        }

        return boardToBuild;
    }

}
