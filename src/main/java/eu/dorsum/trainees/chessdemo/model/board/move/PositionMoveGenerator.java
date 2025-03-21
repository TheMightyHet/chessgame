package eu.dorsum.trainees.chessdemo.model.board.move;

import eu.dorsum.trainees.chessdemo.model.board.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PositionMoveGenerator {

    public List<String> getPossibleMoves(BitBoard bitBoard, String position) {
        List<String> allMoves = new ArrayList<>();

        // Handle regular piece moves
        List<String> regularMoves = getBasicPossibleMoves(bitBoard, position);
        allMoves.addAll(regularMoves);

        // Check for castling if this is a king
        int square = NotationConverter.cellNotationToIndex(position);
        long squareBitboard = 1L << square;

        Piece pieceOnSquare = null;
        for (Map.Entry<Piece, Long> entry : MoveManager.getBitboards(bitBoard).entrySet()) {
            if ((entry.getValue() & squareBitboard) != 0) {
                pieceOnSquare = entry.getKey();
                break;
            }
        }

        if (pieceOnSquare != null && PieceType.KING.equals(pieceOnSquare.getType())) {
            // If it's a king, check for castling moves
            PieceColourType kingColor = pieceOnSquare.getColor();

            // Make sure it's the player's turn
            if ((kingColor == PieceColourType.WHITE && bitBoard.isWhiteTurn()) ||
                    (kingColor == PieceColourType.BLACK && !bitBoard.isWhiteTurn())) {

                long allFriendlyPieces = MoveManager.getAllPiecesOfColor(bitBoard, kingColor);
                long allPieces = MoveManager.getAllPieces(bitBoard);

                KingMoveGenerator kingMoveGenerator = new KingMoveGenerator();
                long castlingMoves = kingMoveGenerator.getCastlingMoves(
                        bitBoard, squareBitboard, allFriendlyPieces, allPieces, kingColor);

                // Convert castling moves to notation
                List<String> castlingSquares = NotationConverter.bitboardToCellNotations(castlingMoves);
                allMoves.addAll(castlingSquares);
            }
        }

        // Filter moves that would leave own king in check
        return filterSafeMoves(bitBoard, position, allMoves);
    }

    private List<String> getBasicPossibleMoves(BitBoard bitBoard, String position) {
        int square = NotationConverter.cellNotationToIndex(position);
        long squareBitboard = 1L << square;

        Map<Piece, Long> bitboards = MoveManager.getBitboards(bitBoard);

        Piece pieceOnSquare = null;
        for (Map.Entry<Piece, Long> entry : bitboards.entrySet()) {
            if ((entry.getValue() & squareBitboard) != 0) {
                pieceOnSquare = entry.getKey();
                break;
            }
        }

        if (pieceOnSquare == null) {
            return new ArrayList<>();
        }

        MoveGenerator moveGenerator = MoveGeneratorFactory.getMoveGenerator(pieceOnSquare);

        long allFriendlyPieces = MoveManager.getAllPiecesOfColor(bitBoard, pieceOnSquare.getColor());
        long allEnemyPieces = MoveManager.getAllPiecesOfColor(bitBoard,
                PieceColourType.WHITE.equals(pieceOnSquare.getColor()) ? PieceColourType.BLACK : PieceColourType.WHITE);

        long possibleMovesBitboard = moveGenerator.getPossibleMovesForPiece(
                squareBitboard, allFriendlyPieces, allEnemyPieces, pieceOnSquare);

        return NotationConverter.bitboardToCellNotations(possibleMovesBitboard);
    }

    private List<String> filterSafeMoves(BitBoard bitBoard, String fromPosition, List<String> allMoves) {
        int fromSquare = NotationConverter.cellNotationToIndex(fromPosition);
        long fromBitboard = 1L << fromSquare;

        Piece movingPiece = null;
        for (Map.Entry<Piece, Long> entry : MoveManager.getBitboards(bitBoard).entrySet()) {
            if ((entry.getValue() & fromBitboard) != 0) {
                movingPiece = entry.getKey();
                break;
            }
        }

        if (movingPiece == null) {
            return new ArrayList<>();
        }

        // Make sure we only consider moves for the current player's turn
        PieceColourType pieceColor = movingPiece.getColor();
        if ((pieceColor == PieceColourType.WHITE && !bitBoard.isWhiteTurn()) ||
                (pieceColor == PieceColourType.BLACK && !bitBoard.isBlackTurn())) {
            return new ArrayList<>();
        }

        List<String> safeMoves = new ArrayList<>();

        for (String toPosition : allMoves) {
            int toSquare = NotationConverter.cellNotationToIndex(toPosition);
            long toBitboard = 1L << toSquare;

            // Create a temporary board to test the move
            BitBoard tempBoard = MoveManager.copyBitBoard(bitBoard);

            // Check if it's a capture
            boolean isCapture = false;
            Piece capturedPiece = MoveManager.getPieceAt(tempBoard, toBitboard);
            if (capturedPiece != null && capturedPiece.getColor() != pieceColor) {
                isCapture = true;
                MoveManager.removePieceAt(tempBoard, toBitboard);
            }

            // Apply the move on the temp board using MoveManager
            MoveManager.updatePiecePosition(tempBoard, movingPiece, fromBitboard, toBitboard);

            // Check if king would be in check
            if (!MoveManager.isKingInCheck(tempBoard, pieceColor)) {
                safeMoves.add(toPosition);
            }
        }

        return safeMoves;
    }

    public List<String> getPossibleMovesNotation(BitBoard bitBoard, String position) {
        List<String> destinations = getPossibleMoves(bitBoard, position);
        List<String> fullNotations = new ArrayList<>();

        if (destinations.isEmpty()) {
            return fullNotations;
        }

        int square = NotationConverter.cellNotationToIndex(position);
        long squareBitboard = 1L << square;

        Map<Piece, Long> bitboards = MoveManager.getBitboards(bitBoard);
        Piece pieceOnSquare = null;
        for (Map.Entry<Piece, Long> entry : bitboards.entrySet()) {
            if ((entry.getValue() & squareBitboard) != 0) {
                pieceOnSquare = entry.getKey();
                break;
            }
        }

        if (pieceOnSquare == null) {
            return fullNotations;
        }

        // King's castling destinations
        boolean isKing = pieceOnSquare.getType() == PieceType.KING;
        boolean isWhite = pieceOnSquare.getColor() == PieceColourType.WHITE;

        // Constants for castling destinations
        long kingsideDest = isWhite ? 0x40L : 0x4000000000000000L;  // g1 or g8
        long queensideDest = isWhite ? 0x4L : 0x400000000000000L;   // c1 or c8

        char pieceChar = getPieceChar(pieceOnSquare);

        long allEnemyPieces = MoveManager.getAllPiecesOfColor(bitBoard,
                pieceOnSquare.getColor() == PieceColourType.WHITE ? PieceColourType.BLACK : PieceColourType.WHITE);

        for (String destination : destinations) {
            int destinationSquare = NotationConverter.cellNotationToIndex(destination);
            long destinationBitboard = 1L << destinationSquare;

            // Check for castling - special notation
            if (isKing) {
                if (destinationBitboard == kingsideDest) {
                    fullNotations.add("O-O");
                    continue;
                } else if (destinationBitboard == queensideDest) {
                    fullNotations.add("O-O-O");
                    continue;
                }
            }

            boolean isCapture = (destinationBitboard & allEnemyPieces) != 0;

            String notation = pieceChar + position + (isCapture ? "x" : "") + destination;
            fullNotations.add(notation);
        }

        return fullNotations;
    }

    private char getPieceChar(Piece piece) {
        return switch (piece.getType()) {
            case PAWN -> piece.getColor() == PieceColourType.WHITE ? 'P' : 'p';
            case KNIGHT -> piece.getColor() == PieceColourType.WHITE ? 'N' : 'n';
            case BISHOP -> piece.getColor() == PieceColourType.WHITE ? 'B' : 'b';
            case ROOK -> piece.getColor() == PieceColourType.WHITE ? 'R' : 'r';
            case QUEEN -> piece.getColor() == PieceColourType.WHITE ? 'Q' : 'q';
            case KING -> piece.getColor() == PieceColourType.WHITE ? 'K' : 'k';
        };
    }
}