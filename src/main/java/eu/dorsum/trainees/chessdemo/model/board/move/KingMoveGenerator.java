package eu.dorsum.trainees.chessdemo.model.board.move;

import eu.dorsum.trainees.chessdemo.model.board.BitBoard;
import eu.dorsum.trainees.chessdemo.model.board.MoveManager;
import eu.dorsum.trainees.chessdemo.model.board.Piece;
import eu.dorsum.trainees.chessdemo.model.board.PieceColourType;

public class KingMoveGenerator implements MoveGenerator {

    // Castling constants - squares that must be empty for castling
    private static final long WHITE_KINGSIDE_PATH = 0x60L;                // f1-g1 (squares between king and rook)
    private static final long WHITE_QUEENSIDE_PATH = 0xEL;                // b1-c1-d1 (squares between king and rook)
    private static final long BLACK_KINGSIDE_PATH = 0x6000000000000000L;  // f8-g8
    private static final long BLACK_QUEENSIDE_PATH = 0xE00000000000000L;  // b8-c8-d8

    // Squares that king traverses during castling (must not be under attack)
    private static final long WHITE_KINGSIDE_KING_PATH = 0x60L;           // e1-f1-g1
    private static final long WHITE_QUEENSIDE_KING_PATH = 0x1CL;          // e1-d1-c1
    private static final long BLACK_KINGSIDE_KING_PATH = 0x6000000000000000L;  // e8-f8-g8
    private static final long BLACK_QUEENSIDE_KING_PATH = 0x1C00000000000000L; // e8-d8-c8

    // King and rook start positions
    private static final long WHITE_KING_START = 0x10L;                  // e1
    private static final long WHITE_KINGSIDE_ROOK = 0x80L;               // h1
    private static final long WHITE_QUEENSIDE_ROOK = 0x1L;               // a1
    private static final long BLACK_KING_START = 0x1000000000000000L;    // e8
    private static final long BLACK_KINGSIDE_ROOK = 0x8000000000000000L; // h8
    private static final long BLACK_QUEENSIDE_ROOK = 0x100000000000000L; // a8

    // King destinations after castling
    private static final long WHITE_KING_KINGSIDE = 0x40L;               // g1
    private static final long WHITE_KING_QUEENSIDE = 0x4L;               // c1
    private static final long BLACK_KING_KINGSIDE = 0x4000000000000000L; // g8
    private static final long BLACK_KING_QUEENSIDE = 0x400000000000000L; // c8

    @Override
    public long getPossibleMovesForPiece(long piecePosition, long allFriendlyPieces, long allEnemyPieces, Piece piece) {
        long possibleMoves = 0L;

        int square = Long.numberOfTrailingZeros(piecePosition);
        int file = square % 8;
        int rank = square / 8;

        int[][] directions = {
                {0, 1},    // north (up)
                {1, 1},    // northeast
                {1, 0},    // east (right)
                {1, -1},   // southeast
                {0, -1},   // south (down)
                {-1, -1},  // southwest
                {-1, 0},   // west (left)
                {-1, 1}    // northwest
        };

        for (int[] direction : directions) {
            int targetFile = file + direction[0];
            int targetRank = rank + direction[1];

            if (targetFile >= 0 && targetFile < 8 && targetRank >= 0 && targetRank < 8) {
                int targetSquare = targetRank * 8 + targetFile;
                long targetBit = 1L << targetSquare;

                if ((targetBit & allFriendlyPieces) == 0) {
                    possibleMoves |= targetBit;
                }
            }
        }

        return possibleMoves;
    }

    public long getCastlingMoves(BitBoard bitBoard, long kingPosition, long allFriendlyPieces,
                                 long allPieces, PieceColourType kingColor) {
        if (bitBoard.isInCheck(kingColor)) {
            return 0L; // Cannot castle while in check
        }

        long castlingMoves = 0L;
        boolean isWhite = kingColor == PieceColourType.WHITE;

        // Check if king is on starting square
        if ((isWhite && kingPosition == WHITE_KING_START) ||
                (!isWhite && kingPosition == BLACK_KING_START)) {

            // Kingside castling
            if (canCastleKingside(bitBoard, allFriendlyPieces, allPieces, isWhite)) {
                castlingMoves |= isWhite ? WHITE_KING_KINGSIDE : BLACK_KING_KINGSIDE;
            }

            // Queenside castling
            if (canCastleQueenside(bitBoard, allFriendlyPieces, allPieces, isWhite)) {
                castlingMoves |= isWhite ? WHITE_KING_QUEENSIDE : BLACK_KING_QUEENSIDE;
            }
        }

        return castlingMoves;
    }

    private boolean canCastleKingside(BitBoard bitBoard, long allFriendlyPieces, long allPieces, boolean isWhite) {
        // Check castling rights
        if (!bitBoard.hasCastlingRight(isWhite ? BitBoard.WHITE_KINGSIDE : BitBoard.BLACK_KINGSIDE)) {
            return false;
        }

        // Check if path is clear (no pieces between king and rook)
        long pathToCheck = isWhite ? WHITE_KINGSIDE_PATH : BLACK_KINGSIDE_PATH;
        if ((allPieces & pathToCheck) != 0) {
            return false;
        }

        // Check if rook is present
        long rookPosition = isWhite ? WHITE_KINGSIDE_ROOK : BLACK_KINGSIDE_ROOK;
        long rookBitboard = bitBoard.getBitBoard(isWhite ? Piece.WHITE_ROOK : Piece.BLACK_ROOK);
        if ((rookBitboard & rookPosition) == 0) {
            return false;
        }

        // Check that king doesn't move through or into check
        // This must be checked by the MoveManager, as we don't have access to
        // the full attack maps here

        return true;
    }

    private boolean canCastleQueenside(BitBoard bitBoard, long allFriendlyPieces, long allPieces, boolean isWhite) {
        // Check castling rights
        if (!bitBoard.hasCastlingRight(isWhite ? BitBoard.WHITE_QUEENSIDE : BitBoard.BLACK_QUEENSIDE)) {
            return false;
        }

        // Check if path is clear (no pieces between king and rook)
        long pathToCheck = isWhite ? WHITE_QUEENSIDE_PATH : BLACK_QUEENSIDE_PATH;
        if ((allPieces & pathToCheck) != 0) {
            return false;
        }

        // Check if rook is present
        long rookPosition = isWhite ? WHITE_QUEENSIDE_ROOK : BLACK_QUEENSIDE_ROOK;
        long rookBitboard = bitBoard.getBitBoard(isWhite ? Piece.WHITE_ROOK : Piece.BLACK_ROOK);
        if ((rookBitboard & rookPosition) == 0) {
            return false;
        }

        // Check that king doesn't move through or into check
        // This must be checked by the MoveManager

        return true;
    }

    public static long getKingCastlingPath(boolean isWhite, boolean isKingside) {
        if (isWhite) {
            return isKingside ? WHITE_KINGSIDE_KING_PATH : WHITE_QUEENSIDE_KING_PATH;
        } else {
            return isKingside ? BLACK_KINGSIDE_KING_PATH : BLACK_QUEENSIDE_KING_PATH;
        }
    }

    public static long getRookStartPosition(boolean isWhite, boolean isKingside) {
        if (isWhite) {
            return isKingside ? WHITE_KINGSIDE_ROOK : WHITE_QUEENSIDE_ROOK;
        } else {
            return isKingside ? BLACK_KINGSIDE_ROOK : BLACK_QUEENSIDE_ROOK;
        }
    }

    public static long getRookEndPosition(boolean isWhite, boolean isKingside) {
        // After castling, rook moves to:
        // - Kingside: f1/f8 (next to the king)
        // - Queenside: d1/d8 (next to the king)
        if (isWhite) {
            return isKingside ? 0x20L : 0x8L; // f1 or d1
        } else {
            return isKingside ? 0x2000000000000000L : 0x800000000000000L; // f8 or d8
        }
    }

    public static boolean isCastlingMove(long fromSquare, long toSquare, PieceColourType kingColor) {
        boolean isWhite = kingColor == PieceColourType.WHITE;

        // King must be at starting position
        if (fromSquare != (isWhite ? WHITE_KING_START : BLACK_KING_START)) {
            return false;
        }

        // Check if destination is a castling destination
        return toSquare == (isWhite ? WHITE_KING_KINGSIDE : BLACK_KING_KINGSIDE) ||
                toSquare == (isWhite ? WHITE_KING_QUEENSIDE : BLACK_KING_QUEENSIDE);
    }

    public static boolean isKingsideCastling(long toSquare, PieceColourType kingColor) {
        boolean isWhite = kingColor == PieceColourType.WHITE;
        return toSquare == (isWhite ? WHITE_KING_KINGSIDE : BLACK_KING_KINGSIDE);
    }
}