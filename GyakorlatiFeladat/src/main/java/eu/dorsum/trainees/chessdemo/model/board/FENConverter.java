package eu.dorsum.trainees.chessdemo.model.board;

import java.util.*;

public class FENConverter {
    private Map<Piece, Long> bitboards;

    private static final List<Piece> WHITE_PIECES = Arrays.asList(
            Piece.WHITE_PAWN, Piece.WHITE_KNIGHT, Piece.WHITE_BISHOP,
            Piece.WHITE_ROOK, Piece.WHITE_QUEEN, Piece.WHITE_KING
    );

    private static final List<Piece> BLACK_PIECES = Arrays.asList(
            Piece.BLACK_PAWN, Piece.BLACK_KNIGHT, Piece.BLACK_BISHOP,
            Piece.BLACK_ROOK, Piece.BLACK_QUEEN, Piece.BLACK_KING
    );

    public FENConverter() {
        bitboards = new HashMap<>();
        initializeBoard();
    }

    private void initializeBoard() {
        // White pieces
        bitboards.put(Piece.WHITE_PAWN, 0x000000000000FF00L);    // a2-h2
        bitboards.put(Piece.WHITE_KNIGHT, 0x0000000000000042L);  // b1, g1
        bitboards.put(Piece.WHITE_BISHOP, 0x0000000000000024L);  // c1, f1
        bitboards.put(Piece.WHITE_ROOK, 0x0000000000000081L);    // a1, h1
        bitboards.put(Piece.WHITE_QUEEN, 0x0000000000000008L);   // d1
        bitboards.put(Piece.WHITE_KING, 0x0000000000000010L);    // e1

        // Black pieces
        bitboards.put(Piece.BLACK_PAWN, 0x00FF000000000000L);    // a7-h7
        bitboards.put(Piece.BLACK_KNIGHT, 0x4200000000000000L);  // b8, g8
        bitboards.put(Piece.BLACK_BISHOP, 0x2400000000000000L);  // c8, f8
        bitboards.put(Piece.BLACK_ROOK, 0x8100000000000000L);    // a8, h8
        bitboards.put(Piece.BLACK_QUEEN, 0x0800000000000000L);   // d8
        bitboards.put(Piece.BLACK_KING, 0x1000000000000000L);    // e8
    }

    public void applyMove(String notation) {
        NotationConverter.MoveInfo moveInfo = NotationConverter.parseNotation(notation);

        // Verify piece is at the expected position
        if ((bitboards.get(moveInfo.getPiece()) & moveInfo.getFromBitboard()) == 0) {
            throw new IllegalArgumentException("No " + moveInfo.getPiece() + " at " +
                    NotationConverter.indexToCellNotation(moveInfo.getFromIndex()));
        }

        // Handle capture if needed
        if (moveInfo.isCapture()) {
            handleCapture(moveInfo.getPiece(), moveInfo.getToIndex());
        }

        // Update the moving piece's bitboard
        movePiece(moveInfo.getPiece(), moveInfo.getFromBitboard(), moveInfo.getToBitboard());
    }

    private void movePiece(Piece piece, long fromBit, long toBit) {
        long bitboard = bitboards.get(piece);
        // Clear the from bit and set the to bit
        bitboard = (bitboard & ~fromBit) | toBit;
        bitboards.put(piece, bitboard);
    }

    private void handleCapture(Piece movingPiece, int toIndex) {
        long toBit = 1L << toIndex;
        List<Piece> opponentPieces = movingPiece.getColor() == PieceColourType.BLACK ? WHITE_PIECES : BLACK_PIECES;

        for (Piece piece : opponentPieces) {
            if ((bitboards.get(piece) & toBit) != 0) {
                // Clear the bit for the captured piece
                bitboards.put(piece, bitboards.get(piece) & ~toBit);
                break;
            }
        }
    }

    public Map<Piece, Long> getBitboards() {
        return new HashMap<>(bitboards);
    }

    public List<String> getPieceCellNotations(Piece piece) {
        Long bitboard = bitboards.get(piece);
        if (bitboard == null) {
            return Collections.emptyList();
        }
        return NotationConverter.bitboardToCellNotations(bitboard);
    }
}