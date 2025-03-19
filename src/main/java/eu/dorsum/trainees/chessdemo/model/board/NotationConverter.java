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

    public static boolean isCheckMate(String notation) {
        if (notation == null || notation.isEmpty()) {
            return false;
        }
        return notation.charAt(notation.length() - 1) == '#';
    }

    public static boolean isSimpleMove(String notation) {
        if (notation == null || notation.isEmpty()) {
            return false;
        }
        char lastChar = notation.charAt(notation.length() - 1);
        return lastChar != '/' && lastChar != '#';
    }

    public static boolean hasCapture(String notation) {
        if (notation == null || notation.isEmpty()) {
            return false;
        }
        return notation.contains("x");
    }

    public static boolean isCastlingMove(String notation) {
        return notation != null && (notation.equals("O-O/") || notation.equals("O-O-O/") ||
                notation.equals("0-0/") || notation.equals("0-0-0/") ||
                notation.equals("O-O") || notation.equals("O-O-O") ||
                notation.equals("0-0") || notation.equals("0-0-0"));
    }

    public static boolean isKingsideCastling(String notation) {
        return notation != null && (notation.equals("O-O/") || notation.equals("0-0/") ||
                notation.equals("O-O") || notation.equals("0-0"));
    }

    public static CastlingInfo parseCastlingNotation(String notation, boolean isWhite) {
        boolean isKingside = isKingsideCastling(notation);

        // Get king's start and end positions
        int kingFromIndex = isWhite ? 4 : 60; // e1 or e8
        int kingToIndex = isWhite
                ? (isKingside ? 6 : 2)   // g1 or c1
                : (isKingside ? 62 : 58); // g8 or c8

        // Get rook's start and end positions
        int rookFromIndex = isWhite
                ? (isKingside ? 7 : 0)   // h1 or a1
                : (isKingside ? 63 : 56); // h8 or a8

        int rookToIndex = isWhite
                ? (isKingside ? 5 : 3)   // f1 or d1
                : (isKingside ? 61 : 59); // f8 or d8

        // Create result object
        Piece king = isWhite ? Piece.WHITE_KING : Piece.BLACK_KING;
        Piece rook = isWhite ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;

        return new CastlingInfo(
                king, kingFromIndex, kingToIndex,
                rook, rookFromIndex, rookToIndex,
                isKingside
        );
    }

    public static MoveInfo parseNotation(String notation) {
        // Check for castling notation
        if (isCastlingMove(notation)) {
            boolean isWhite = true; // Default to white - this will be overridden by MoveManager
            return new MoveInfo(
                    isWhite ? Piece.WHITE_KING : Piece.BLACK_KING,
                    -1, -1, false, true, isKingsideCastling(notation)
            );
        }

        // Regular move notation
        validateNotationFormat(notation);

        Piece movingPiece = extractMovingPiece(notation);
        boolean isCapture = isCapture(notation);
        String fromSquare = extractFromSquare(notation);
        String toSquare = extractToSquare(notation, isCapture);

        int fromIndex = cellNotationToIndex(fromSquare);
        int toIndex = cellNotationToIndex(toSquare);

        return new MoveInfo(movingPiece, fromIndex, toIndex, isCapture, false, false);
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
        private final boolean isCastling;
        private final boolean isKingsideCastling;

        public MoveInfo(Piece piece, int fromIndex, int toIndex, boolean isCapture) {
            this(piece, fromIndex, toIndex, isCapture, false, false);
        }

        public MoveInfo(Piece piece, int fromIndex, int toIndex, boolean isCapture,
                        boolean isCastling, boolean isKingsideCastling) {
            this.piece = piece;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.isCapture = isCapture;
            this.isCastling = isCastling;
            this.isKingsideCastling = isKingsideCastling;
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

        public boolean isCastling() {
            return isCastling;
        }

        public boolean isKingsideCastling() {
            return isKingsideCastling;
        }

        public long getFromBitboard() {
            return 1L << fromIndex;
        }

        public long getToBitboard() {
            return 1L << toIndex;
        }
    }

    public static class CastlingInfo {
        private final Piece king;
        private final int kingFromIndex;
        private final int kingToIndex;
        private final Piece rook;
        private final int rookFromIndex;
        private final int rookToIndex;
        private final boolean isKingside;

        public CastlingInfo(Piece king, int kingFromIndex, int kingToIndex,
                            Piece rook, int rookFromIndex, int rookToIndex,
                            boolean isKingside) {
            this.king = king;
            this.kingFromIndex = kingFromIndex;
            this.kingToIndex = kingToIndex;
            this.rook = rook;
            this.rookFromIndex = rookFromIndex;
            this.rookToIndex = rookToIndex;
            this.isKingside = isKingside;
        }

        public Piece getKing() {
            return king;
        }

        public int getKingFromIndex() {
            return kingFromIndex;
        }

        public int getKingToIndex() {
            return kingToIndex;
        }

        public Piece getRook() {
            return rook;
        }

        public int getRookFromIndex() {
            return rookFromIndex;
        }

        public int getRookToIndex() {
            return rookToIndex;
        }

        public boolean isKingside() {
            return isKingside;
        }

        public long getKingFromBitboard() {
            return 1L << kingFromIndex;
        }

        public long getKingToBitboard() {
            return 1L << kingToIndex;
        }

        public long getRookFromBitboard() {
            return 1L << rookFromIndex;
        }

        public long getRookToBitboard() {
            return 1L << rookToIndex;
        }
    }
}