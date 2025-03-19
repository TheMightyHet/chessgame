package eu.dorsum.trainees.chessdemo.model.board;

import eu.dorsum.trainees.chessdemo.model.board.move.MoveGenerator;
import eu.dorsum.trainees.chessdemo.model.board.move.MoveGeneratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class MoveManager {
    private static final Logger log = LoggerFactory.getLogger(MoveManager.class);

    private MoveManager() {}

    public static void debugSquare(BitBoard bitBoard, String squareNotation) {
        int index = NotationConverter.cellNotationToIndex(squareNotation);
        long squareBit = 1L << index;

        log.info("Debugging square: {}", squareNotation);

        Piece piece = getPieceAt(bitBoard, squareBit);
        if (piece != null) {
            log.info("Found {} at {}", piece, squareNotation);
        } else {
            log.info("No pieces found at {}", squareNotation);
        }
    }

    public static boolean applyMove(BitBoard bitBoard, String notation) {
        try {
            if (!isCorrectPlayerTurn(bitBoard, notation)) {
                log.info("Move failed: Not this player's turn");
                return false;
            }

            String parsableNotation = prepareNotation(bitBoard, notation);
            boolean isCapture = NotationConverter.hasCapture(notation);

            NotationConverter.MoveInfo moveInfo = NotationConverter.parseNotation(parsableNotation);

            if (!isPieceAtPosition(bitBoard, moveInfo)) {
                return false;
            }

            String fromSquare = NotationConverter.indexToCellNotation(moveInfo.getFromIndex());
            String toSquare = NotationConverter.indexToCellNotation(moveInfo.getToIndex());
            logMoveAttempt(bitBoard, moveInfo, fromSquare, toSquare);

            boolean shouldCapture = determineIfCapture(bitBoard, moveInfo, isCapture);

            if (!isMoveValid(bitBoard, moveInfo)) {
                log.info("Move failed: Not a valid move for this piece");
                return false;
            }

            executeMove(bitBoard, moveInfo, shouldCapture);

            // Switch turns after successful move
            switchTurn(bitBoard);

            log.info("Move successful: {} moved from {} to {}. Turn switched to {}",
                    moveInfo.getPiece(), fromSquare, toSquare,
                    bitBoard.isWhiteTurn() ? "White" : "Black");
            return true;

        } catch (Exception e) {
            log.info("Move failed with exception: {}", e.getMessage());
            return false;
        }
    }

    private static boolean isCorrectPlayerTurn(BitBoard bitBoard, String notation) {
        // Extract the piece character from the notation
        char pieceChar = notation.charAt(0);
        boolean isWhitePiece = Character.isUpperCase(pieceChar);

        // Check if the piece color matches the current turn
        return (isWhitePiece && bitBoard.isWhiteTurn()) ||
                (!isWhitePiece && bitBoard.isBlackTurn());
    }

    private static void switchTurn(BitBoard bitBoard) {
        // Toggle the turn state in BitBoard
        bitBoard.toggleTurn();
    }

    private static String prepareNotation(BitBoard bitBoard, String notation) {
        if (NotationConverter.isCheckMate(notation)) {
            bitBoard.setInCheckmate(true);
            return notation.substring(0, notation.length() - 1) + "/";
        } else if (NotationConverter.isSimpleMove(notation)) {
            return notation + "/";
        }
        return notation;
    }

    private static boolean isPieceAtPosition(BitBoard bitBoard, NotationConverter.MoveInfo moveInfo) {
        if ((bitBoard.getBitBoard(moveInfo.getPiece()) & moveInfo.getFromBitboard()) == 0) {
            log.info("Move failed: No piece at specified position");
            return false;
        }
        return true;
    }

    private static void logMoveAttempt(BitBoard bitBoard, NotationConverter.MoveInfo moveInfo, String fromSquare, String toSquare) {
        log.info("Attempting move: {} from {} to {}", moveInfo.getPiece(), fromSquare, toSquare);
        log.info("Target square before move:");
        debugSquare(bitBoard, toSquare);
    }

    private static boolean determineIfCapture(BitBoard bitBoard, NotationConverter.MoveInfo moveInfo, boolean isCapture) {
        boolean shouldCapture = isCapture || moveInfo.isCapture();
        int toIndex = moveInfo.getToIndex();
        long toBit = 1L << toIndex;
        String toSquare = NotationConverter.indexToCellNotation(toIndex);

        Piece pieceAtDestination = getPieceAt(bitBoard, toBit);

        if (pieceAtDestination != null && pieceAtDestination.getColor() != moveInfo.getPiece().getColor()) {
            log.info("Detected {} at destination square {}", pieceAtDestination, toSquare);
            if (!shouldCapture) {
                log.info("Treating as capture since there's an enemy piece at destination");
                shouldCapture = true;
            }
        }

        return shouldCapture;
    }

    private static void executeMove(BitBoard bitBoard, NotationConverter.MoveInfo moveInfo, boolean shouldCapture) {
        String toSquare = NotationConverter.indexToCellNotation(moveInfo.getToIndex());

        if (shouldCapture) {
            log.info("Processing capture at {}", toSquare);
            handleCapture(bitBoard, moveInfo.getToBitboard());
        }

        updatePiecePosition(bitBoard, moveInfo.getPiece(), moveInfo.getFromBitboard(), moveInfo.getToBitboard());

        log.info("Target square after move:");
        debugSquare(bitBoard, toSquare);
    }

    private static boolean isMoveValid(BitBoard bitBoard, NotationConverter.MoveInfo moveInfo) {
        Piece piece = moveInfo.getPiece();
        long fromBitboard = moveInfo.getFromBitboard();
        long toBitboard = moveInfo.getToBitboard();

        long allFriendlyPieces = getAllPiecesOfColor(bitBoard, piece.getColor());
        long allEnemyPieces = getAllPiecesOfColor(
                bitBoard,
                piece.getColor() == PieceColourType.WHITE ? PieceColourType.BLACK : PieceColourType.WHITE);

        MoveGenerator moveGenerator = MoveGeneratorFactory.getMoveGenerator(piece);
        long possibleMoves = moveGenerator.getPossibleMovesForPiece(fromBitboard, allFriendlyPieces, allEnemyPieces, piece);

        List<String> possibleSquares = NotationConverter.bitboardToCellNotations(possibleMoves);
        String fromSquare = NotationConverter.indexToCellNotation(moveInfo.getFromIndex());
        String toSquare = NotationConverter.indexToCellNotation(moveInfo.getToIndex());
        log.info("Piece at {} can move to: {}", fromSquare, possibleSquares);
        log.info("Trying to move to: {}", toSquare);

        boolean isValid = (possibleMoves & toBitboard) != 0;
        log.info("Move is {}", isValid ? "valid" : "invalid");
        return isValid;
    }

    private static void handleCapture(BitBoard bitBoard, long toBit) {
        String targetSquare = NotationConverter.indexToCellNotation(Long.numberOfTrailingZeros(toBit));
        Piece capturedPiece = getPieceAt(bitBoard, toBit);

        if (capturedPiece != null) {
            log.info("Capture: {} at {} captured", capturedPiece, targetSquare);
            removePieceAt(bitBoard, toBit);

            // Double-check the piece was removed
            if (getPieceAt(bitBoard, toBit) != null) {
                log.error("ERROR: Failed to remove captured piece from board!");
            }
        } else {
            logNoCapturePieceFound(bitBoard, targetSquare);
        }
    }

    private static void logNoCapturePieceFound(BitBoard bitBoard, String targetSquare) {
        log.info("Warning: Capture move to {} but no piece found there.", targetSquare);
        log.info("Showing all pieces on the board:");

        for (Piece piece : Piece.values()) {
            long bitboard = bitBoard.getBitBoard(piece);
            List<String> positions = NotationConverter.bitboardToCellNotations(bitboard);
            log.info("{}: {}", piece, positions);
        }
    }

    public static boolean isInCheckmate(BitBoard bitBoard) {
        return bitBoard.isInCheckmate();
    }

    public static void resetCheckmate(BitBoard bitBoard) {
        bitBoard.setInCheckmate(false);
    }

    public static Map<Piece, Long> getBitboards(BitBoard bitBoard) {
        return new HashMap<>(bitBoard.boards); // Note: Direct access to 'boards' requires it to be public or a getter added
    }

    public static List<String> getPieceCellNotations(BitBoard bitBoard, Piece piece) {
        long bitboard = bitBoard.getBitBoard(piece);
        return NotationConverter.bitboardToCellNotations(bitboard);
    }

    public static Piece getPieceAt(BitBoard bitBoard, long position) {
        Map<Piece, Long> boards = bitBoard.boards;
        for (Map.Entry<Piece, Long> entry : boards.entrySet()) {
            if ((entry.getValue() & position) != 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void updatePiecePosition(BitBoard bitBoard, Piece piece, long fromBit, long toBit) {
        Map<Piece, Long> boards = bitBoard.boards;
        long bitboard = boards.get(piece);
        bitboard = (bitboard & ~fromBit) | toBit;
        boards.put(piece, bitboard);
    }

    public static void removePieceAt(BitBoard bitBoard, long position) {
        Map<Piece, Long> boards = bitBoard.boards;
        Piece piece = getPieceAt(bitBoard, position);
        if (piece != null) {
            long bitboard = boards.get(piece);
            bitboard &= ~position;
            boards.put(piece, bitboard);
        }
    }

    public static long getAllPiecesOfColor(BitBoard bitBoard, PieceColourType color) {
        Map<Piece, Long> boards = bitBoard.boards;
        long result = 0L;
        for (Map.Entry<Piece, Long> entry : boards.entrySet()) {
            if (entry.getKey().getColor() == color) {
                result |= entry.getValue();
            }
        }
        return result;
    }
}