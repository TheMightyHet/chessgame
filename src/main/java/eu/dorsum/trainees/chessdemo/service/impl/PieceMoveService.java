package eu.dorsum.trainees.chessdemo.service.impl;

import eu.dorsum.trainees.chessdemo.model.board.MoveManager;
import eu.dorsum.trainees.chessdemo.model.board.Piece;
import eu.dorsum.trainees.chessdemo.model.board.move.PositionMoveGenerator;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MoveService {

    private final MoveManager moveManager;
    private final PositionMoveGenerator positionMoveGenerator;

    public MoveService() {
        this.moveManager = new MoveManager();
        this.positionMoveGenerator = new PositionMoveGenerator(moveManager);
    }

    public List<String> getPossibleMoves(String position) {
        return positionMoveGenerator.getPossibleMoves(position);
    }

    public List<String> getPossibleMovesWithNotation(String position) {
        return positionMoveGenerator.getPossibleMovesNotation(position);
    }

    public boolean applyMove(String notation) {
        return moveManager.applyMove(notation);
    }

    public Map<Piece, Long> getBoardState() {
        return moveManager.getBitboards();
    }

    public List<String> getPiecePositions(Piece piece) {
        return moveManager.getPieceCellNotations(piece);
    }

}