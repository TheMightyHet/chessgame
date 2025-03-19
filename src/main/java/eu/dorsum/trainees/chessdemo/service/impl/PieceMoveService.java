package eu.dorsum.trainees.chessdemo.service.impl;

import eu.dorsum.trainees.chessdemo.model.board.BitBoard;
import eu.dorsum.trainees.chessdemo.model.board.MoveManager;
import eu.dorsum.trainees.chessdemo.model.board.Piece;
import eu.dorsum.trainees.chessdemo.model.board.PieceColourType;
import eu.dorsum.trainees.chessdemo.model.board.move.PositionMoveGenerator;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PieceMoveService {

    private final BitBoard bitBoard;
    private final PositionMoveGenerator positionMoveGenerator;

    public PieceMoveService() {
        this.bitBoard = new BitBoard();
        this.positionMoveGenerator = new PositionMoveGenerator();
        this.bitBoard.initBoard(
                "WR/WN/WB/WQ/WK/WB/WN/WR/WP/WP/WP/WP/WP/WP/WP/WP/////////////////////////////////BP/BP/BP/BP/BP/BP/BP/BP/BR/BN/BB/BQ/BK/BB/BN/BR",
                "W");
    }

    public List<String> getPossibleMoves(String position) {
        return positionMoveGenerator.getPossibleMoves(bitBoard, position);
    }

    public List<String> getPossibleMovesWithNotation(String position) {
        return positionMoveGenerator.getPossibleMovesNotation(bitBoard, position);
    }

    public boolean applyMove(String notation) {
        return MoveManager.applyMove(bitBoard, notation);
    }

    public Map<Piece, Long> getBoardState() {
        return MoveManager.getBitboards(bitBoard);
    }

    public List<String> getPiecePositions(Piece piece) {
        return MoveManager.getPieceCellNotations(bitBoard, piece);
    }

    public char[][] getBoardMatrix() {
        return bitBoard.getBoardMatrix();
    }

    public String getBoardMatrixString() {
        return bitBoard.getBoardMatrixString();
    }

    public BitBoard getBitBoard() {
        return this.bitBoard;
    }

    public PieceColourType getCurrentTurn() {
        return bitBoard.getCurrentPlayerColor();
    }

    public boolean isInCheck(PieceColourType kingColor) {
        return bitBoard.isInCheck(kingColor);
    }

    public boolean isInCheckmate(PieceColourType playerColor) {
        return bitBoard.isInCheckmate(playerColor);
    }

    public boolean isKingInCheck(PieceColourType kingColor) {
        return MoveManager.isKingInCheck(bitBoard, kingColor);
    }
}