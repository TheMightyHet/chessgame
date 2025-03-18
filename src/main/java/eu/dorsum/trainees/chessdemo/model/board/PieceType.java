package eu.dorsum.trainees.chessdemo.model.board;


public enum PieceType {
    PAWN(0), KNIGHT(1), BISHOP(2), ROOK(3), QUEEN(4), KING(5);

    int value;

    PieceType(int pawnTypeID) {
        value = pawnTypeID;
    }

    public int getValue(){
        return value;
    }
}