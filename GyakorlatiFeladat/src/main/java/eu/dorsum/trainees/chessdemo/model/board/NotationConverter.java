package eu.dorsum.trainees.chessdemo.model.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotationConverter {
    private static final Map<Character, Piece> PIECE_CHAR_MAP = new HashMap<>();

    static {
        PIECE_CHAR_MAP.put('P', Piece.WHITE_PAWN);
        PIECE_CHAR_MAP.put('N', Piece.WHITE_KNIGHT);
        PIECE_CHAR_MAP.put('B', Piece.WHITE_BISHOP);
        PIECE_CHAR_MAP.put('R', Piece.WHITE_ROOK);
        PIECE_CHAR_MAP.put('Q', Piece.WHITE_QUEEN);
        PIECE_CHAR_MAP.put('K', Piece.WHITE_KING);
        PIECE_CHAR_MAP.put('p', Piece.BLACK_PAWN);
        PIECE_CHAR_MAP.put('n', Piece.BLACK_KNIGHT);
        PIECE_CHAR_MAP.put('b', Piece.BLACK_BISHOP);
        PIECE_CHAR_MAP.put('r', Piece.BLACK_ROOK);
        PIECE_CHAR_MAP.put('q', Piece.BLACK_QUEEN);
        PIECE_CHAR_MAP.put('k', Piece.BLACK_KING);
    }

    public static MoveInfo parseNotation(String notation) {
        validateNotationFormat(notation);

        Piece movingPiece = extractMovingPiece(notation);
        boolean isCapture = isCapture(notation);
        String fromSquare = extractFromSquare(notation);
        String toSquare = extractToSquare(notation, isCapture);

        int fromIndex = cellNotationToIndex(fromSquare);
        int toIndex = cellNotationToIndex(toSquare);

        return new MoveInfo(movingPiece, fromIndex, toIndex, isCapture);
    }

    public static long indexToBitboard(int index) {
        if (index < 0 || index > 63) {
            throw new IllegalArgumentException("Invalid square index: " + index);
        }
        return 1L << index;
    }

    public static int cellNotationToIndex(String square) {
        if (square.length() != 2) {
            throw new IllegalArgumentException("Invalid square format: " + square);
        }
        char file = square.charAt(0);
        char rank = square.charAt(1);
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Invalid square coordinates: " + square);
        }
        int fileIndex = file - 'a';
        int rankIndex = rank - '1';
        return rankIndex * 8 + fileIndex;
    }

    public static String indexToCellNotation(int index) {
        if (index < 0 || index > 63) {
            throw new IllegalArgumentException("Invalid square index: " + index);
        }
        char file = (char) ('a' + (index % 8));
        char rank = (char) ('1' + (index / 8));
        return "" + file + rank;
    }

    public static List<String> bitboardToCellNotations(long bitboard) {
        List<String> cells = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            if ((bitboard & (1L << i)) != 0) {
                cells.add(indexToCellNotation(i));
            }
        }
        return cells;
    }

    private static Piece extractMovingPiece(String notation) {
        char pieceChar = notation.charAt(0);
        Piece movingPiece = PIECE_CHAR_MAP.get(pieceChar);
        if (movingPiece == null) {
            throw new IllegalArgumentException("Invalid piece character: " + pieceChar);
        }
        return movingPiece;
    }

    private static void validateNotationFormat(String notation) {
        int len = notation.length();
        if (len != 6 && len != 7) {
            throw new IllegalArgumentException("Invalid notation length: " + notation);
        }
        if (notation.charAt(len - 1) != '/') {
            throw new IllegalArgumentException("Missing trailing slash: " + notation);
        }
        if (len == 7 && notation.charAt(3) != 'x') {
            throw new IllegalArgumentException("Invalid capture format: " + notation);
        }
    }

    private static boolean isCapture(String notation) {
        return notation.length() == 7;
    }

    private static String extractFromSquare(String notation) {
        return notation.substring(1, 3);
    }

    private static String extractToSquare(String notation, boolean isCapture) {
        return isCapture ? notation.substring(4, 6) : notation.substring(3, 5);
    }

    public static class MoveInfo {
        private final Piece piece;
        private final int fromIndex;
        private final int toIndex;
        private final boolean isCapture;

        public MoveInfo(Piece piece, int fromIndex, int toIndex, boolean isCapture) {
            this.piece = piece;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.isCapture = isCapture;
        }

        public Piece getPiece() {
            return piece;
        }

        public int getFromIndex() {
            return fromIndex;
        }

        public int getToIndex() {
            return toIndex;
        }

        public boolean isCapture() {
            return isCapture;
        }

        public long getFromBitboard() {
            return 1L << fromIndex;
        }

        public long getToBitboard() {
            return 1L << toIndex;
        }
    }
}