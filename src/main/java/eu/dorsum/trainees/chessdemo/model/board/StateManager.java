package eu.dorsum.trainees.chessdemo.model.board;

public class StateManager {

    public StateManager(){}

    public long getPiecesOfType(String state, Piece piece){

        long boardToBuild = 0x0000000000000000;
        String[] squares = state.split("/");

        for (int i = 0; i < squares.length; ++i){

            if(squares[i].isEmpty()) {continue;}
            if(
                    squares[i].charAt(0) == 'W' && piece.getColor() == PieceColourType.WHITE &&
                            squares[i].charAt(1) == piece.getType().getSymbol() ||
                            squares[i].charAt(0) == 'B' && piece.getColor() == PieceColourType.BLACK &&
                                    squares[i].charAt(1) == piece.getType().getSymbol()
            ) {
                boardToBuild |= (1L << i);
            }
        }

        return boardToBuild;
    }

}