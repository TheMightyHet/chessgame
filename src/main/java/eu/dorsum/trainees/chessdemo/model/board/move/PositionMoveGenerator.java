package eu.dorsum.trainees.chessdemo.model.board.move;

import eu.dorsum.trainees.chessdemo.model.board.BitBoard;
import eu.dorsum.trainees.chessdemo.model.board.MoveManager;
import eu.dorsum.trainees.chessdemo.model.board.NotationConverter;
import eu.dorsum.trainees.chessdemo.model.board.Piece;
import eu.dorsum.trainees.chessdemo.model.board.PieceColourType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PositionMoveGenerator {

    public List<String> getPossibleMoves(BitBoard bitBoard, String position) {
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

        char pieceChar = getPieceChar(pieceOnSquare);

        long allEnemyPieces = MoveManager.getAllPiecesOfColor(bitBoard,
                pieceOnSquare.getColor() == PieceColourType.WHITE ? PieceColourType.BLACK : PieceColourType.WHITE);

        for (String destination : destinations) {
            int destinationSquare = NotationConverter.cellNotationToIndex(destination);
            long destinationBitboard = 1L << destinationSquare;

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