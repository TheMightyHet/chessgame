package eu.dorsum.trainees.chessdemo.model.board;

public class Piece {
    // Static constants for commonly used pieces
    public static final Piece WHITE_PAWN = new Piece(PieceType.PAWN, PieceColourType.WHITE);
    public static final Piece WHITE_KNIGHT = new Piece(PieceType.KNIGHT, PieceColourType.WHITE);
    public static final Piece WHITE_BISHOP = new Piece(PieceType.BISHOP, PieceColourType.WHITE);
    public static final Piece WHITE_ROOK = new Piece(PieceType.ROOK, PieceColourType.WHITE);
    public static final Piece WHITE_QUEEN = new Piece(PieceType.QUEEN, PieceColourType.WHITE);
    public static final Piece WHITE_KING = new Piece(PieceType.KING, PieceColourType.WHITE);

    public static final Piece BLACK_PAWN = new Piece(PieceType.PAWN, PieceColourType.BLACK);
    public static final Piece BLACK_KNIGHT = new Piece(PieceType.KNIGHT, PieceColourType.BLACK);
    public static final Piece BLACK_BISHOP = new Piece(PieceType.BISHOP, PieceColourType.BLACK);
    public static final Piece BLACK_ROOK = new Piece(PieceType.ROOK, PieceColourType.BLACK);
    public static final Piece BLACK_QUEEN = new Piece(PieceType.QUEEN, PieceColourType.BLACK);
    public static final Piece BLACK_KING = new Piece(PieceType.KING, PieceColourType.BLACK);

    private final PieceType type;
    private final PieceColourType color;

    public Piece(PieceType type, PieceColourType color) {
        this.type = type;
        this.color = color;
    }

    public static Piece[] values() {
        return new Piece[] {
                WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING,
                BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING
        };
    }

    public PieceType getType() {
        return type;
    }

    public PieceColourType getColor() {
        return color;
    }

    public static Piece getPiece(PieceType type, PieceColourType color) {
        if (color == PieceColourType.WHITE) {
            switch (type) {
                case PAWN: return WHITE_PAWN;
                case KNIGHT: return WHITE_KNIGHT;
                case BISHOP: return WHITE_BISHOP;
                case ROOK: return WHITE_ROOK;
                case QUEEN: return WHITE_QUEEN;
                case KING: return WHITE_KING;
            }
        } else {
            switch (type) {
                case PAWN: return BLACK_PAWN;
                case KNIGHT: return BLACK_KNIGHT;
                case BISHOP: return BLACK_BISHOP;
                case ROOK: return BLACK_ROOK;
                case QUEEN: return BLACK_QUEEN;
                case KING: return BLACK_KING;
            }
        }
        throw new IllegalArgumentException("Unknown piece type/color combination");
    }

    @Override
    public String toString() {
        return type.toString() + " " + color.toString();
    }
}