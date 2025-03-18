package eu.dorsum.trainees.chessdemo.model.board.move;

import eu.dorsum.trainees.chessdemo.model.board.Piece;

public class KingMoveGenerator implements MoveGenerator {
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

        // TODO: Add castling logic

        return possibleMoves;
    }
}