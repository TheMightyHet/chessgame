package eu.dorsum.trainees.chessdemo.model.board;

import eu.dorsum.trainees.chessdemo.model.board.move.KingMoveGenerator;
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

            // Check for castling
            if (NotationConverter.isCastlingMove(parsableNotation)) {
                return handleCastlingMove(bitBoard, parsableNotation);
            }

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

            // Check if move would leave own king in check
            BitBoard tempBoard = copyBitBoard(bitBoard);
            if (shouldCapture) {
                handleCaptureTempBoard(tempBoard, moveInfo.getToBitboard());
            }
            updatePiecePositionTempBoard(tempBoard, moveInfo.getPiece(), moveInfo.getFromBitboard(), moveInfo.getToBitboard());

            if (isKingInCheck(tempBoard, moveInfo.getPiece().getColor())) {
                log.info("Move failed: Would leave own king in check");
                return false;
            }

            executeMove(bitBoard, moveInfo, shouldCapture);

            // Update castling rights if king or rook moves
            updateCastlingRights(bitBoard, moveInfo);

            // Switch turns after successful move
            switchTurn(bitBoard);

            // Update check status for both kings after move
            boolean isWhiteInCheck = isKingInCheck(bitBoard, PieceColourType.WHITE);
            boolean isBlackInCheck = isKingInCheck(bitBoard, PieceColourType.BLACK);

            bitBoard.setInCheck(PieceColourType.WHITE, isWhiteInCheck);
            bitBoard.setInCheck(PieceColourType.BLACK, isBlackInCheck);

            // Check for checkmate
            if (isWhiteInCheck) {
                boolean isWhiteInCheckmate = isInCheckmate(bitBoard, PieceColourType.WHITE);
                bitBoard.setInCheckmate(PieceColourType.WHITE, isWhiteInCheckmate);
                if (isWhiteInCheckmate) {
                    log.info("White king is in CHECKMATE! Black wins!");
                } else {
                    log.info("White king is in check!");
                }
            } else {
                bitBoard.setInCheckmate(PieceColourType.WHITE, false);
            }

            if (isBlackInCheck) {
                boolean isBlackInCheckmate = isInCheckmate(bitBoard, PieceColourType.BLACK);
                bitBoard.setInCheckmate(PieceColourType.BLACK, isBlackInCheckmate);
                if (isBlackInCheckmate) {
                    log.info("Black king is in CHECKMATE! White wins!");
                } else {
                    log.info("Black king is in check!");
                }
            } else {
                bitBoard.setInCheckmate(PieceColourType.BLACK, false);
            }

            log.info("Move successful: {} moved from {} to {}. Turn switched to {}",
                    moveInfo.getPiece(), fromSquare, toSquare,
                    bitBoard.isWhiteTurn() ? "White" : "Black");
            return true;

        } catch (Exception e) {
            log.info("Move failed with exception: {}", e.getMessage());
            return false;
        }
    }

    private static boolean handleCastlingMove(BitBoard bitBoard, String notation) {
        // Get the current player's color
        PieceColourType playerColor = bitBoard.getCurrentPlayerColor();
        boolean isWhite = playerColor == PieceColourType.WHITE;

        // Parse the castling move
        NotationConverter.CastlingInfo castlingInfo = NotationConverter.parseCastlingNotation(
                notation, isWhite);

        // Check castling rights
        int castlingRightFlag = isWhite
                ? (castlingInfo.isKingside() ? BitBoard.WHITE_KINGSIDE : BitBoard.WHITE_QUEENSIDE)
                : (castlingInfo.isKingside() ? BitBoard.BLACK_KINGSIDE : BitBoard.BLACK_QUEENSIDE);

        if (!bitBoard.hasCastlingRight(castlingRightFlag)) {
            log.info("Castling failed: No castling rights");
            return false;
        }

        // Check if squares between king and rook are empty
        long kingPos = 1L << castlingInfo.getKingFromIndex();
        long rookPos = 1L << castlingInfo.getRookFromIndex();

        // Get all pieces on the board
        long allPieces = getAllPieces(bitBoard);

        // Path must be clear (excluding king and rook themselves)
        long pathToCheck = getBetweenSquares(castlingInfo.getKingFromIndex(), castlingInfo.getRookFromIndex());
        if ((allPieces & pathToCheck) != 0) {
            log.info("Castling failed: Path between king and rook is not clear");
            return false;
        }

        // Check if king is in check (already checked in the calling method)
        if (bitBoard.isInCheck(playerColor)) {
            log.info("Castling failed: King is in check");
            return false;
        }

        // Check if king passes through check
        long kingPath = KingMoveGenerator.getKingCastlingPath(isWhite, castlingInfo.isKingside());
        if (wouldKingPassThroughCheck(bitBoard, kingPath, playerColor)) {
            log.info("Castling failed: King would pass through check");
            return false;
        }

        // Execute the castling move
        log.info("Executing {} castling for {}",
                castlingInfo.isKingside() ? "kingside" : "queenside",
                isWhite ? "white" : "black");

        // Move king
        long kingFromBit = 1L << castlingInfo.getKingFromIndex();
        long kingToBit = 1L << castlingInfo.getKingToIndex();
        updatePiecePosition(bitBoard, castlingInfo.getKing(), kingFromBit, kingToBit);

        // Move rook
        long rookFromBit = 1L << castlingInfo.getRookFromIndex();
        long rookToBit = 1L << castlingInfo.getRookToIndex();
        updatePiecePosition(bitBoard, castlingInfo.getRook(), rookFromBit, rookToBit);

        // Remove all castling rights for this color
        if (isWhite) {
            bitBoard.removeCastlingRight(BitBoard.WHITE_KINGSIDE | BitBoard.WHITE_QUEENSIDE);
        } else {
            bitBoard.removeCastlingRight(BitBoard.BLACK_KINGSIDE | BitBoard.BLACK_QUEENSIDE);
        }

        // Switch turns after successful move
        switchTurn(bitBoard);

        // Update check status for both kings after move
        boolean isWhiteInCheck = isKingInCheck(bitBoard, PieceColourType.WHITE);
        boolean isBlackInCheck = isKingInCheck(bitBoard, PieceColourType.BLACK);

        bitBoard.setInCheck(PieceColourType.WHITE, isWhiteInCheck);
        bitBoard.setInCheck(PieceColourType.BLACK, isBlackInCheck);

        log.info("Castling successful. Turn switched to {}",
                bitBoard.isWhiteTurn() ? "White" : "Black");

        return true;
    }

    private static long getBetweenSquares(int from, int to) {
        long result = 0L;

        // Assuming this is only called for straight lines (rank, file, or diagonal)
        int fromRank = from / 8;
        int fromFile = from % 8;
        int toRank = to / 8;
        int toFile = to % 8;

        // If on same rank
        if (fromRank == toRank) {
            int minFile = Math.min(fromFile, toFile);
            int maxFile = Math.max(fromFile, toFile);
            for (int f = minFile + 1; f < maxFile; f++) {
                result |= 1L << (fromRank * 8 + f);
            }
        }
        // If on same file
        else if (fromFile == toFile) {
            int minRank = Math.min(fromRank, toRank);
            int maxRank = Math.max(fromRank, toRank);
            for (int r = minRank + 1; r < maxRank; r++) {
                result |= 1L << (r * 8 + fromFile);
            }
        }
        // If on diagonal (not needed for castling but included for completeness)
        else if (Math.abs(fromRank - toRank) == Math.abs(fromFile - toFile)) {
            int rankStep = (toRank > fromRank) ? 1 : -1;
            int fileStep = (toFile > fromFile) ? 1 : -1;

            int r = fromRank + rankStep;
            int f = fromFile + fileStep;
            while (r != toRank && f != toFile) {
                result |= 1L << (r * 8 + f);
                r += rankStep;
                f += fileStep;
            }
        }

        return result;
    }

    private static boolean wouldKingPassThroughCheck(BitBoard bitBoard, long kingPath,
                                                     PieceColourType kingColor) {
        // For each square on the path, check if it's under attack
        long remainingPath = kingPath;
        while (remainingPath != 0) {
            long squareBit = remainingPath & -remainingPath; // Get next bit
            remainingPath &= ~squareBit; // Remove it from remaining

            // Create a temp board with king on this square
            BitBoard tempBoard = copyBitBoard(bitBoard);

            // Find the king's current position
            long kingCurrentPos = tempBoard.getBitBoard(
                    kingColor == PieceColourType.WHITE ? Piece.WHITE_KING : Piece.BLACK_KING);

            // Move king to this position temporarily
            updatePiecePositionTempBoard(tempBoard,
                    kingColor == PieceColourType.WHITE ? Piece.WHITE_KING : Piece.BLACK_KING,
                    kingCurrentPos, squareBit);

            // Check if king would be in check
            if (isKingInCheck(tempBoard, kingColor)) {
                return true; // King would be in check
            }
        }

        return false; // Path is safe
    }

    private static void updateCastlingRights(BitBoard bitBoard, NotationConverter.MoveInfo moveInfo) {
        Piece piece = moveInfo.getPiece();
        int fromIndex = moveInfo.getFromIndex();

        // If king moves, remove all castling rights for that color
        if (piece == Piece.WHITE_KING) {
            bitBoard.removeCastlingRight(BitBoard.WHITE_KINGSIDE | BitBoard.WHITE_QUEENSIDE);
        } else if (piece == Piece.BLACK_KING) {
            bitBoard.removeCastlingRight(BitBoard.BLACK_KINGSIDE | BitBoard.BLACK_QUEENSIDE);
        }

        // If rook moves, remove corresponding castling right
        else if (piece == Piece.WHITE_ROOK) {
            if (fromIndex == 0) { // a1 - queenside rook
                bitBoard.removeCastlingRight(BitBoard.WHITE_QUEENSIDE);
            } else if (fromIndex == 7) { // h1 - kingside rook
                bitBoard.removeCastlingRight(BitBoard.WHITE_KINGSIDE);
            }
        } else if (piece == Piece.BLACK_ROOK) {
            if (fromIndex == 56) { // a8 - queenside rook
                bitBoard.removeCastlingRight(BitBoard.BLACK_QUEENSIDE);
            } else if (fromIndex == 63) { // h8 - kingside rook
                bitBoard.removeCastlingRight(BitBoard.BLACK_KINGSIDE);
            }
        }

        // If a rook is captured, remove corresponding castling right
        int toIndex = moveInfo.getToIndex();
        if (toIndex == 0) { // a1
            bitBoard.removeCastlingRight(BitBoard.WHITE_QUEENSIDE);
        } else if (toIndex == 7) { // h1
            bitBoard.removeCastlingRight(BitBoard.WHITE_KINGSIDE);
        } else if (toIndex == 56) { // a8
            bitBoard.removeCastlingRight(BitBoard.BLACK_QUEENSIDE);
        } else if (toIndex == 63) { // h8
            bitBoard.removeCastlingRight(BitBoard.BLACK_KINGSIDE);
        }
    }

    private static boolean isCorrectPlayerTurn(BitBoard bitBoard, String notation) {
        // Castling notation doesn't have piece character, so handle separately
        if (notation.equals("O-O") || notation.equals("O-O-O") ||
                notation.equals("0-0") || notation.equals("0-0-0")) {
            // Check if it's the right player's turn for castling
            return true; // Will be validated in handleCastlingMove
        }

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
        // Handle castling notation
        if (notation.equals("O-O") || notation.equals("O-O-O") ||
                notation.equals("0-0") || notation.equals("0-0-0")) {
            return notation + "/";
        }

        if (NotationConverter.isCheckMate(notation)) {
            bitBoard.setInCheckmate(true);
            return notation.substring(0, notation.length() - 1) + "/";
        } else if (NotationConverter.isSimpleMove(notation)) {
            return notation + "/";
        }
        return notation;
    }

    private static boolean isPieceAtPosition(BitBoard bitBoard, NotationConverter.MoveInfo moveInfo) {
        if (moveInfo.isCastling()) {
            return true; // Special case for castling
        }

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

        // For king, we need to check castling moves as well
        if (piece.getType() == PieceType.KING) {
            long allPieces = allFriendlyPieces | allEnemyPieces;
            KingMoveGenerator kingMoveGenerator = (KingMoveGenerator) moveGenerator;
            possibleMoves |= kingMoveGenerator.getCastlingMoves(
                    bitBoard, fromBitboard, allFriendlyPieces, allPieces, piece.getColor());
        }

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
        return new HashMap<>(bitBoard.boards);
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

    private static void updatePiecePositionTempBoard(BitBoard tempBoard, Piece piece, long fromBit, long toBit) {
        long bitboard = tempBoard.getBitBoard(piece);
        bitboard = (bitboard & ~fromBit) | toBit;
        tempBoard.boards.put(piece, bitboard);
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

    private static void handleCaptureTempBoard(BitBoard tempBoard, long toBit) {
        Piece capturedPiece = getPieceAt(tempBoard, toBit);
        if (capturedPiece != null) {
            removePieceAt(tempBoard, toBit);
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

    public static long getAllPieces(BitBoard bitBoard) {
        return getAllPiecesOfColor(bitBoard, PieceColourType.WHITE) |
                getAllPiecesOfColor(bitBoard, PieceColourType.BLACK);
    }

    public static BitBoard copyBitBoard(BitBoard original) {
        BitBoard copy = new BitBoard();

        // Copy all bitboards
        for (Piece p : Piece.values()) {
            copy.boards.put(p, original.getBitBoard(p));
        }

        // Copy other relevant state
        copy.setTurn(original.isWhiteTurn());
        copy.setCastlingRights(original.getCastlingRights());

        return copy;
    }

    public static boolean isInCheckmate(BitBoard bitBoard, PieceColourType kingColor) {
        // First check if the king is in check - if not, can't be checkmate
        if (!isKingInCheck(bitBoard, kingColor)) {
            return false;
        }

        // Get all pieces of the player's color
        for (Piece piece : Piece.values()) {
            if (piece.getColor() != kingColor) {
                continue;
            }

            long pieceBitboard = bitBoard.getBitBoard(piece);
            if (pieceBitboard == 0) {
                continue; // No pieces of this type
            }

            // For each piece of this type
            long remainingPieces = pieceBitboard;
            while (remainingPieces != 0) {
                // Get next piece position
                long piecePosition = remainingPieces & -remainingPieces;
                remainingPieces &= ~piecePosition;

                // Check if this piece can make any move that would get out of check
                if (canPieceMakeValidMove(bitBoard, piece, piecePosition, kingColor)) {
                    return false;
                }
            }
        }

        // If no piece can make any move to get out of check, it's checkmate
        return true;
    }

    private static boolean canPieceMakeValidMove(BitBoard bitBoard, Piece piece, long piecePosition, PieceColourType kingColor) {
        // Get all possible moves for this piece
        long allFriendlyPieces = getAllPiecesOfColor(bitBoard, kingColor);
        long allEnemyPieces = getAllPiecesOfColor(bitBoard,
                kingColor == PieceColourType.WHITE ? PieceColourType.BLACK : PieceColourType.WHITE);

        MoveGenerator moveGenerator = MoveGeneratorFactory.getMoveGenerator(piece);
        long possibleMoves = moveGenerator.getPossibleMovesForPiece(
                piecePosition, allFriendlyPieces, allEnemyPieces, piece);

        // For king, add castling moves
        if (piece.getType() == PieceType.KING) {
            long allPieces = allFriendlyPieces | allEnemyPieces;
            KingMoveGenerator kingMoveGenerator = (KingMoveGenerator) moveGenerator;
            possibleMoves |= kingMoveGenerator.getCastlingMoves(
                    bitBoard, piecePosition, allFriendlyPieces, allPieces, kingColor);
        }

        // No moves possible
        if (possibleMoves == 0) {
            return false;
        }

        // Try each possible move
        long remainingMoves = possibleMoves;
        while (remainingMoves != 0) {
            long movePosition = remainingMoves & -remainingMoves;
            remainingMoves &= ~movePosition;

            // Create temp board for move simulation
            BitBoard tempBoard = copyBitBoard(bitBoard);

            // Check if it's a capture
            if ((movePosition & allEnemyPieces) != 0) {
                handleCaptureTempBoard(tempBoard, movePosition);
            }

            // Apply the move on the temp board
            updatePiecePositionTempBoard(tempBoard, piece, piecePosition, movePosition);

            // If the king is no longer in check after this move, this is a valid escape move
            if (!isKingInCheck(tempBoard, kingColor)) {
                return true;
            }
        }

        // No valid moves found
        return false;
    }

    public static boolean isKingInCheck(BitBoard bitBoard, PieceColourType kingColor) {
        Piece king = kingColor == PieceColourType.WHITE ? Piece.WHITE_KING : Piece.BLACK_KING;
        long kingPosition = bitBoard.getBitBoard(king);

        if (kingPosition == 0) {
            return false; // No king on board (shouldn't happen in a valid game)
        }

        PieceColourType enemyColor = (kingColor == PieceColourType.WHITE)
                ? PieceColourType.BLACK : PieceColourType.WHITE;

        long allFriendlyPieces = getAllPiecesOfColor(bitBoard, kingColor);
        long allEnemyPieces = getAllPiecesOfColor(bitBoard, enemyColor);

        // Check each enemy piece to see if it can attack the king
        for (Piece piece : Piece.values()) {
            if (piece.getColor() != enemyColor) {
                continue; // Skip pieces of wrong color
            }

            long pieceBitboard = bitBoard.getBitBoard(piece);
            if (pieceBitboard == 0) {
                continue; // Skip if no pieces of this type on board
            }

            // For each piece of this type on the board
            long remainingPieces = pieceBitboard;
            while (remainingPieces != 0) {
                // Get next piece position
                long piecePosition = remainingPieces & -remainingPieces; // Isolate least significant bit
                remainingPieces &= ~piecePosition; // Remove from remaining pieces

                // Get move generator for this piece type
                MoveGenerator moveGenerator = MoveGeneratorFactory.getMoveGenerator(piece);

                // Get possible moves for this piece
                long possibleMoves = moveGenerator.getPossibleMovesForPiece(
                        piecePosition, allEnemyPieces, allFriendlyPieces, piece);

                // If king's position is in possible moves, the king is in check
                if ((possibleMoves & kingPosition) != 0) {
                    return true;
                }
            }
        }

        return false;
    }
}