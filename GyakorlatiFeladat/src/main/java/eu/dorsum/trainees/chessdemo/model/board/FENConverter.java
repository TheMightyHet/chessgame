package eu.dorsum.trainees.chessdemo.model.board;

import java.util.*;

public class FENConverter {
    private Map<Piece, String> bitboards;
    private static final Map<Character, Piece> PIECE_CHAR_MAP = new HashMap<>();

    private static final List<Piece> WHITE_PIECES = Arrays.asList(
            Piece.WHITE_PAWN, Piece.WHITE_KNIGHT, Piece.WHITE_BISHOP,
            Piece.WHITE_ROOK, Piece.WHITE_QUEEN, Piece.WHITE_KING
    );

    private static final List<Piece> BLACK_PIECES = Arrays.asList(
            Piece.BLACK_PAWN, Piece.BLACK_KNIGHT, Piece.BLACK_BISHOP,
            Piece.BLACK_ROOK, Piece.BLACK_QUEEN, Piece.BLACK_KING
    );

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

    public FENConverter() {
        bitboards = new HashMap<>();
        initializeBoard();
    }

    private void initializeBoard() {
        // White pieces
        bitboards.put(Piece.WHITE_PAWN, "000000000000FF00");    // a2-h2
        bitboards.put(Piece.WHITE_KNIGHT, "0000000000000042");  // b1
        bitboards.put(Piece.WHITE_BISHOP, "0000000000000024");  // c1
        bitboards.put(Piece.WHITE_ROOK, "0000000000000081");    // a1
        bitboards.put(Piece.WHITE_QUEEN, "0000000000000008");   // d1
        bitboards.put(Piece.WHITE_KING, "0000000000000010");    // e1

        // Black pieces
        bitboards.put(Piece.BLACK_PAWN, "00FF000000000000");    // a7-h7
        bitboards.put(Piece.BLACK_KNIGHT, "4200000000000000");  // b8
        bitboards.put(Piece.BLACK_BISHOP, "2400000000000000");  // c8
        bitboards.put(Piece.BLACK_ROOK, "8100000000000000");    // a8
        bitboards.put(Piece.BLACK_QUEEN, "0800000000000000");   // d8
        bitboards.put(Piece.BLACK_KING, "1000000000000000");    // e8
    }
    public void applyMove(String notation) {
        validateNotationFormat(notation);

        Piece movingPiece = extractMovingPiece(notation);
        boolean isCapture = isCapture(notation);

        String fromSquare = extractFromSquare(notation);
        String toSquare = extractToSquare(notation, isCapture);

        int fromIndex = cellNotationToIndex(fromSquare);
        int toIndex = cellNotationToIndex(toSquare);

        movePiece(movingPiece, fromIndex, toIndex);

        if (isCapture) {
            handleCapture(movingPiece, toIndex);
        }
    }

    private void validateNotationFormat(String notation) {
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

    private Piece extractMovingPiece(String notation) {
        char pieceChar = notation.charAt(0);
        Piece movingPiece = PIECE_CHAR_MAP.get(pieceChar);
        if (movingPiece == null) {
            throw new IllegalArgumentException("Invalid piece character: " + pieceChar);
        }
        return movingPiece;
    }

    private boolean isCapture(String notation) {
        return notation.length() == 7;
    }

    private String extractFromSquare(String notation) {
        return notation.substring(1, 3);
    }

    private String extractToSquare(String notation, boolean isCapture) {
        return isCapture ? notation.substring(4, 6) : notation.substring(3, 5);
    }

    private void movePiece(Piece piece, int fromIndex, int toIndex) {
        String bitboard = bitboards.get(piece);
        if (bitboard == null) {
            throw new IllegalArgumentException("No bitboard found for piece: " + piece);
        }
        bitboard = clearBit(bitboard, fromIndex);
        bitboard = setBit(bitboard, toIndex);
        bitboards.put(piece, bitboard);
    }

    private void handleCapture(Piece movingPiece, int toIndex) {
        List<Piece> opponentPieces = movingPiece.getColor() == PieceColourType.BLACK ? WHITE_PIECES : BLACK_PIECES;
        for (Piece piece : opponentPieces) {
            String bb = bitboards.get(piece);
            if (isBitSet(bb, toIndex)) {
                bb = clearBit(bb, toIndex);
                bitboards.put(piece, bb);
                break;
            }
        }
    }

    int cellNotationToIndex(String square) {
        if (square.length() != 2) {
            throw new IllegalArgumentException("Invalid square format: " + square);
        }
        char row = square.charAt(0);
        char col = square.charAt(1);
        if (row < 'a' || row > 'h' || col < '1' || col > '8') {
            throw new IllegalArgumentException("Invalid square coordinates: " + square);
        }
        int rowIndex = row - 'a';
        int colIndex = col - '1';
        return colIndex * 8 + rowIndex;
    }

    String setBit(String hex, int i) {
        int digitIndex = (63 - i) / 4;
        int bitPos = 3 - ((63 - i) % 4);
        char c = hex.charAt(digitIndex);
        int val = Character.digit(c, 16);
        val |= (1 << bitPos);
        char newC = Character.toUpperCase(Character.forDigit(val, 16));
        return hex.substring(0, digitIndex) + newC + hex.substring(digitIndex + 1);
    }

    String clearBit(String hex, int i) {
        int digitIndex = (63 - i) / 4;
        int bitPos = 3 - ((63 - i) % 4);
        char c = hex.charAt(digitIndex);
        int val = Character.digit(c, 16);
        val &= ~(1 << bitPos);
        char newC = Character.toUpperCase(Character.forDigit(val, 16));
        return hex.substring(0, digitIndex) + newC + hex.substring(digitIndex + 1);
    }

    boolean isBitSet(String hex, int i) {
        int digitIndex = (63 - i) / 4;
        int bitPos = 3 - ((63 - i) % 4);
        char c = hex.charAt(digitIndex);
        int val = Character.digit(c, 16);
        return (val & (1 << bitPos)) != 0;
    }

    public Map<Piece, String> getBitboards() {
        return new HashMap<>(bitboards);
    }
}