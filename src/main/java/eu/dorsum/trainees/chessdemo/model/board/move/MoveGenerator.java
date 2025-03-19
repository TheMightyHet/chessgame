package eu.dorsum.trainees.chessdemo.model.board.move;

import eu.dorsum.trainees.chessdemo.model.board.Piece;

public interface MoveGenerator {

    long getPossibleMovesForPiece(long piecePosition, long allFriendlyPieces, long allEnemyPieces, Piece piece);
}