package eu.dorsum.trainees.chessdemo.model.board.move;

import eu.dorsum.trainees.chessdemo.model.board.Piece;

public class KnightMoveGenerator implements MoveGenerator {
    @Override
    public long getPossibleMovesForPiece(long piecePosition, long allFriendlyPieces, long allEnemyPieces, Piece piece) {
        long possibleMoves = 0L;

        int square = Long.numberOfTrailingZeros(piecePosition);
        int file = square % 8;
        int rank = square / 8;

        int[][] knightMoves = {
                {-2, -1}, {-2, 1},  // Left 2, up/down 1
                {-1, -2}, {-1, 2},  // Left 1, up/down 2
                {1, -2}, {1, 2},    // Right 1, up/down 2
                {2, -1}, {2, 1}     // Right 2, up/down 1
        };

        for (int[] move : knightMoves) {
            int targetFile = file + move[0];
            int targetRank = rank + move[1];

            if (targetFile >= 0 && targetFile < 8 && targetRank >= 0 && targetRank < 8) {
                int targetSquare = targetRank * 8 + targetFile;
                long targetBit = 1L << targetSquare;

                if ((targetBit & allFriendlyPieces) == 0) {
                    possibleMoves |= targetBit;
                }
            }
        }

        return possibleMoves;
    }
}