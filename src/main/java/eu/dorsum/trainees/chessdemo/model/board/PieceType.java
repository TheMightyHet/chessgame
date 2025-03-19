package eu.dorsum.trainees.chessdemo.model.board;


public enum PieceType {
    PAWN(0, 'P'),
    KNIGHT(1, 'N'),
    BISHOP(2, 'B'),
    ROOK(3, 'R'),
    QUEEN(4, 'Q'),
    KING(5, 'K');

    private int value;
    private char symbol;

    PieceType(int pawnTypeID, char pawnTypeSymbol) {
        value = pawnTypeID;
        symbol = pawnTypeSymbol;
    }

    public int getValue(){
        return value;
    }

    public char getSymbol(){
        return symbol;
    }
}