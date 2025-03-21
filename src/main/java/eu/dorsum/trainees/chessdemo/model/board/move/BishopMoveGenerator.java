package eu.dorsum.trainees.chessdemo.model.board.move;

import eu.dorsum.trainees.chessdemo.model.board.Piece;

public class BishopMoveGenerator implements MoveGenerator {
    @Override
    public long getPossibleMovesForPiece(long piecePosition, long allFriendlyPieces, long allEnemyPieces, Piece piece) {
        long possibleMoves = 0L;

        int square = Long.numberOfTrailingZeros(piecePosition);
        int file = square % 8;
        int rank = square / 8;

        int[][] directions = {
                {1, 1},    // up-right
                {1, -1},   // down-right
                {-1, -1},  // down-left
                {-1, 1}    // up-left
        };

        for (int[] direction : directions) {
            possibleMoves |= calculateDirectionalMoves(file, rank, direction[0], direction[1],
                    allFriendlyPieces, allEnemyPieces);
        }

        return possibleMoves;
    }

    private long calculateDirectionalMoves(int startFile, int startRank, int fileOffset, int rankOffset,
                                           long allFriendlyPieces, long allEnemyPieces) {
        long directionalMoves = 0L;
        int currentFile = startFile;
        int currentRank = startRank;
        boolean continueScanning = true;

        while (continueScanning) {
            currentFile += fileOffset;
            currentRank += rankOffset;

            if (!isSquareOnBoard(currentFile, currentRank)) {
                continueScanning = false;
            } else {
                int targetSquare = currentRank * 8 + currentFile;
                long targetBit = 1L << targetSquare;

                if ((targetBit & allFriendlyPieces) != 0) {
                    continueScanning = false;
                } else {
                    directionalMoves |= targetBit;

                    if ((targetBit & allEnemyPieces) != 0) {
                        continueScanning = false;
                    }
                }
            }
        }

        return directionalMoves;
    }

    private boolean isSquareOnBoard(int file, int rank) {
        return file >= 0 && file < 8 && rank >= 0 && rank < 8;
    }
}