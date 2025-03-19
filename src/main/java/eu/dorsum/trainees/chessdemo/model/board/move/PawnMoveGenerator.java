package eu.dorsum.trainees.chessdemo.model.board.move;

import eu.dorsum.trainees.chessdemo.model.board.Piece;
import eu.dorsum.trainees.chessdemo.model.board.PieceColourType;

public class PawnMoveGenerator implements MoveGenerator {
    @Override
    public long getPossibleMovesForPiece(long piecePosition, long allFriendlyPieces, long allEnemyPieces, Piece piece) {
        boolean isWhite = (piece.getColor() == PieceColourType.WHITE);
        long allPieces = allFriendlyPieces | allEnemyPieces;

        int square = Long.numberOfTrailingZeros(piecePosition);
        int file = square % 8;
        int rank = square / 8;

        int direction = isWhite ? 1 : -1;

        long forwardMoves = calculateForwardMoves(file, rank, direction, allPieces, isWhite);
        long captureMoves = calculateCaptureMoves(file, rank, direction, allEnemyPieces);

        return forwardMoves | captureMoves;
    }

    private long calculateForwardMoves(int file, int rank, int direction, long allPieces, boolean isWhite) {
        long possibleMoves = 0L;

        int targetRank = rank + direction;
        if (!isRankInBounds(targetRank)) {
            return possibleMoves;
        }

        int targetSquare = targetRank * 8 + file;
        long targetBit = 1L << targetSquare;

        if ((allPieces & targetBit) == 0) {
            possibleMoves |= targetBit;

            if (isPawnOnStartingRank(rank, isWhite)) {
                possibleMoves |= calculateDoubleStep(file, targetRank, direction, allPieces);
            }
        }

        return possibleMoves;
    }

    private long calculateDoubleStep(int file, int firstStepRank, int direction, long allPieces) {
        int doubleTargetRank = firstStepRank + direction;

        if (!isRankInBounds(doubleTargetRank)) {
            return 0L;
        }

        int doubleTargetSquare = doubleTargetRank * 8 + file;
        long doubleTargetBit = 1L << doubleTargetSquare;

        if ((allPieces & doubleTargetBit) == 0) {
            return doubleTargetBit;
        }

        return 0L;
    }

    private long calculateCaptureMoves(int file, int rank, int direction, long allEnemyPieces) {
        long possibleMoves = 0L;

        for (int fileOffset = -1; fileOffset <= 1; fileOffset += 2) {
            int captureFile = file + fileOffset;
            int captureRank = rank + direction;

            if (isFileInBounds(captureFile) && isRankInBounds(captureRank)) {
                int captureSquare = captureRank * 8 + captureFile;
                long captureBit = 1L << captureSquare;

                if ((allEnemyPieces & captureBit) != 0) {
                    possibleMoves |= captureBit;
                }
            }
        }

        return possibleMoves;
    }

    private boolean isPawnOnStartingRank(int rank, boolean isWhite) {
        return (isWhite && rank == 1) || (!isWhite && rank == 6);
    }

    private boolean isRankInBounds(int rank) {
        return rank >= 0 && rank < 8;
    }

    private boolean isFileInBounds(int file) {
        return file >= 0 && file < 8;
    }
}