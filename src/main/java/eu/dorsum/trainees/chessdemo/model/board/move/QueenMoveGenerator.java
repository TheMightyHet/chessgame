package eu.dorsum.trainees.chessdemo.model.board.move;

import eu.dorsum.trainees.chessdemo.model.board.Piece;

public class QueenMoveGenerator implements MoveGenerator {
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
            int fileOffset = direction[0];
            int rankOffset = direction[1];

            int currentFile = file;
            int currentRank = rank;

            boolean continueScanning = true;
            while (continueScanning) {
                currentFile += fileOffset;
                currentRank += rankOffset;

                // Check if we're still on the board
                if (!isValidSquare(currentFile, currentRank)) {
                    continueScanning = false;
                } else {
                    int targetSquare = currentRank * 8 + currentFile;
                    long targetBit = 1L << targetSquare;

                    // Check for friendly piece
                    if ((targetBit & allFriendlyPieces) != 0) {
                        continueScanning = false;
                    } else {
                        // Add to possible moves
                        possibleMoves |= targetBit;

                        // Check for enemy piece
                        if ((targetBit & allEnemyPieces) != 0) {
                            continueScanning = false;
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }

    private boolean isValidSquare(int file, int rank) {
        return file >= 0 && file < 8 && rank >= 0 && rank < 8;
    }
}