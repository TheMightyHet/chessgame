package eu.dorsum.trainees.chessdemo.model.board;

import eu.dorsum.trainees.chessdemo.model.board.move.MoveGenerator;
import eu.dorsum.trainees.chessdemo.model.board.move.MoveGeneratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;


public class MoveManager {
    private static final Logger log = LoggerFactory.getLogger(MoveManager.class);
    private Map<Piece, Long> bitboards;
    private boolean isInCheckmate;

    public MoveManager() {
        bitboards = new HashMap<>();
        isInCheckmate = false;
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

    public void debugSquare(String squareNotation) {
        int index = NotationConverter.cellNotationToIndex(squareNotation);
        long squareBit = 1L << index;

        log.info("Debugging square: {}", squareNotation);

        boolean foundPiece = false;
        for (Map.Entry<Piece, Long> entry : bitboards.entrySet()) {
            if ((entry.getValue() & squareBit) != 0) {
                foundPiece = true;
                log.info("Found {} at {}", entry.getKey(), squareNotation);
            }
        }

        if (!foundPiece) {
            log.info("No pieces found at {}", squareNotation);
        }
    }

    public boolean applyMove(String notation) {
        try {
            String parsableNotation = prepareNotation(notation);
            boolean isCapture = NotationConverter.hasCapture(notation);

            NotationConverter.MoveInfo moveInfo = NotationConverter.parseNotation(parsableNotation);

            if (!isPieceAtPosition(moveInfo)) {
                return false;
            }

            String fromSquare = NotationConverter.indexToCellNotation(moveInfo.getFromIndex());
            String toSquare = NotationConverter.indexToCellNotation(moveInfo.getToIndex());
            logMoveAttempt(moveInfo, fromSquare, toSquare);

            boolean shouldCapture = determineIfCapture(moveInfo, isCapture);

            if (!isMoveValid(moveInfo)) {
                log.info("Move failed: Not a valid move for this piece");
                return false;
            }

            executeMove(moveInfo, shouldCapture);

            log.info("Move successful: {} moved from {} to {}", moveInfo.getPiece(), fromSquare, toSquare);
            return true;

        } catch (Exception e) {
            log.info("Move failed with exception: {}", e.getMessage());
            return false;
        }
    }

    private String prepareNotation(String notation) {
        if (NotationConverter.isCheckMate(notation)) {
            isInCheckmate = true;
            return notation.substring(0, notation.length() - 1) + "/";
        }
        else if (NotationConverter.isSimpleMove(notation)) {
            return notation + "/";
        }
        return notation;
    }

    private boolean isPieceAtPosition(NotationConverter.MoveInfo moveInfo) {
        if ((bitboards.get(moveInfo.getPiece()) & moveInfo.getFromBitboard()) == 0) {
            log.info("Move failed: No piece at specified position");
            return false;
        }
        return true;
    }

    private void logMoveAttempt(NotationConverter.MoveInfo moveInfo, String fromSquare, String toSquare) {
        log.info("Attempting move: {} from {} to {}", moveInfo.getPiece(), fromSquare, toSquare);
        log.info("Target square before move:");
        debugSquare(toSquare);
    }

    private boolean determineIfCapture(NotationConverter.MoveInfo moveInfo, boolean isCapture) {
        boolean shouldCapture = isCapture || moveInfo.isCapture();
        int toIndex = moveInfo.getToIndex();
        long toBit = 1L << toIndex;
        String toSquare = NotationConverter.indexToCellNotation(toIndex);

        Piece pieceAtDestination = findPieceAtDestination(toBit, moveInfo.getPiece());

        if (pieceAtDestination != null) {
            log.info("Detected {} at destination square {}", pieceAtDestination, toSquare);
            if (!shouldCapture) {
                log.info("Treating as capture since there's a piece at destination");
                shouldCapture = true;
            }
        }

        return shouldCapture;
    }

    private Piece findPieceAtDestination(long targetBit, Piece excludePiece) {
        for (Map.Entry<Piece, Long> entry : bitboards.entrySet()) {
            if (entry.getKey() != excludePiece && (entry.getValue() & targetBit) != 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void executeMove(NotationConverter.MoveInfo moveInfo, boolean shouldCapture) {
        String toSquare = NotationConverter.indexToCellNotation(moveInfo.getToIndex());

        if (shouldCapture) {
            log.info("Processing capture at {}", toSquare);
            handleCapture(moveInfo.getPiece(), moveInfo.getToIndex());
        }

        movePiece(moveInfo.getPiece(), moveInfo.getFromBitboard(), moveInfo.getToBitboard());

        log.info("Target square after move:");
        debugSquare(toSquare);
    }

    private boolean isMoveValid(NotationConverter.MoveInfo moveInfo) {
        Piece piece = moveInfo.getPiece();
        long fromBitboard = moveInfo.getFromBitboard();
        long toBitboard = moveInfo.getToBitboard();

        long allFriendlyPieces = calculateAllPiecesOfColor(bitboards, piece.getColor());
        long allEnemyPieces = calculateAllPiecesOfColor(bitboards,
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

    private long calculateAllPiecesOfColor(Map<Piece, Long> bitboards, PieceColourType color) {
        long result = 0L;

        for (Map.Entry<Piece, Long> entry : bitboards.entrySet()) {
            if (entry.getKey().getColor() == color) {
                result |= entry.getValue();
            }
        }

        return result;
    }

    private void movePiece(Piece piece, long fromBit, long toBit) {
        long bitboard = bitboards.get(piece);
        bitboard = (bitboard & ~fromBit) | toBit;
        bitboards.put(piece, bitboard);
    }

    private void handleCapture(Piece movingPiece, int toIndex) {
        long toBit = 1L << toIndex;
        String targetSquare = NotationConverter.indexToCellNotation(toIndex);

        Optional<Map.Entry<Piece, Long>> capturedPieceEntry = findCapturedPiece(movingPiece, toBit);

        if (capturedPieceEntry.isPresent()) {
            Piece capturedPiece = capturedPieceEntry.get().getKey();
            long bitboard = capturedPieceEntry.get().getValue();

            log.info("Capture: {} at {} captured by {}", capturedPiece, targetSquare, movingPiece);

            // Clear the bit for the captured piece
            long updatedBitboard = bitboard & ~toBit;
            bitboards.put(capturedPiece, updatedBitboard);

            // Double-check the piece was removed
            if ((bitboards.get(capturedPiece) & toBit) != 0) {
                log.error("ERROR: Failed to remove captured piece from board!");
            }
        } else {
            // No piece found to capture
            logNoCapturePieceFound(targetSquare);
        }
    }

    private Optional<Map.Entry<Piece, Long>> findCapturedPiece(Piece movingPiece, long toBit) {
        return bitboards.entrySet().stream()
                .filter(entry -> entry.getKey() != movingPiece)
                .filter(entry -> (entry.getValue() & toBit) != 0)
                .findFirst();
    }

    private void logNoCapturePieceFound(String targetSquare) {
        log.info("Warning: Capture move to {} but no piece found there.", targetSquare);
        log.info("Showing all pieces on the board:");

        for (Map.Entry<Piece, Long> entry : bitboards.entrySet()) {
            List<String> positions = NotationConverter.bitboardToCellNotations(entry.getValue());
            log.info("{}: {}", entry.getKey(), positions);
        }
    }

    public boolean isInCheckmate() {
        return isInCheckmate;
    }

    public void resetCheckmate() {
        isInCheckmate = false;
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