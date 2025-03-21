package eu.dorsum.trainees.chessdemo.model.board.move;

import eu.dorsum.trainees.chessdemo.model.board.Piece;

public class MoveGeneratorFactory {

    private static final PawnMoveGenerator pawnMoveGenerator = new PawnMoveGenerator();
    private static final KnightMoveGenerator knightMoveGenerator = new KnightMoveGenerator();
    private static final BishopMoveGenerator bishopMoveGenerator = new BishopMoveGenerator();
    private static final RookMoveGenerator rookMoveGenerator = new RookMoveGenerator();
    private static final QueenMoveGenerator queenMoveGenerator = new QueenMoveGenerator();
    private static final KingMoveGenerator kingMoveGenerator = new KingMoveGenerator();

    private MoveGeneratorFactory() {
    }

    public static MoveGenerator getMoveGenerator(Piece piece) {
        return switch (piece.getType()) {
            case PAWN -> pawnMoveGenerator;
            case KNIGHT -> knightMoveGenerator;
            case BISHOP -> bishopMoveGenerator;
            case ROOK -> rookMoveGenerator;
            case QUEEN -> queenMoveGenerator;
            case KING -> kingMoveGenerator;
        };
    }
}