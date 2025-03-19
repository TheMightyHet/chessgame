package eu.dorsum.trainees.chessdemo.model.board;

import java.util.HashMap;
import java.util.Map;

public class BitBoard {
    HashMap<Piece, Long> boards;
    private StateManager stateManager;
    private int castlingRights;
    private boolean whiteTurn;
    private int enPassant;
    private int isCheck;      // Bit 0 = white in check, Bit 1 = black in check
    private int isCheckMate;  // Bit 0 = white in checkmate, Bit 1 = black in checkmate

    // Constants for castling rights
    public static final int WHITE_KINGSIDE = 0x1;
    public static final int WHITE_QUEENSIDE = 0x2;
    public static final int BLACK_KINGSIDE = 0x4;
    public static final int BLACK_QUEENSIDE = 0x8;

    public BitBoard() {
        stateManager = new StateManager();
        boards = new HashMap<>();
        castlingRights = 0b1111; // All castling rights initially available
        whiteTurn = true;
        isCheck = 0b00;
        isCheckMate = 0b00;
    }

    public void initBoard(String boardData, String sideToStart){

        boards.put(Piece.WHITE_PAWN, stateManager.getPiecesOfType(boardData, Piece.WHITE_PAWN));
        boards.put(Piece.BLACK_PAWN, stateManager.getPiecesOfType(boardData, Piece.BLACK_PAWN));

        boards.put(Piece.WHITE_KNIGHT, stateManager.getPiecesOfType(boardData, Piece.WHITE_KNIGHT));
        boards.put(Piece.BLACK_KNIGHT, stateManager.getPiecesOfType(boardData, Piece.BLACK_KNIGHT));

        boards.put(Piece.WHITE_BISHOP, stateManager.getPiecesOfType(boardData, Piece.WHITE_BISHOP));
        boards.put(Piece.BLACK_BISHOP, stateManager.getPiecesOfType(boardData, Piece.BLACK_BISHOP));

        boards.put(Piece.WHITE_ROOK, stateManager.getPiecesOfType(boardData, Piece.WHITE_ROOK));
        boards.put(Piece.BLACK_ROOK, stateManager.getPiecesOfType(boardData, Piece.BLACK_ROOK));

        boards.put(Piece.WHITE_QUEEN, stateManager.getPiecesOfType(boardData, Piece.WHITE_QUEEN));
        boards.put(Piece.BLACK_QUEEN, stateManager.getPiecesOfType(boardData, Piece.BLACK_QUEEN));

        boards.put(Piece.WHITE_KING, stateManager.getPiecesOfType(boardData, Piece.WHITE_KING));
        boards.put(Piece.BLACK_KING, stateManager.getPiecesOfType(boardData, Piece.BLACK_KING));

        if(sideToStart.equals("W")){
            whiteTurn = true;
        } else {
            whiteTurn = false;
        }

        // Reset castling rights
        castlingRights = 0b1111;
    }

    public long getBitBoard(Piece pieceType) {
        return boards.get(pieceType);
    }

    public int getCastlingRights() {
        return castlingRights;
    }

    public boolean hasCastlingRight(int rightFlag) {
        return (castlingRights & rightFlag) != 0;
    }

    public void removeCastlingRight(int rightFlag) {
        castlingRights &= ~rightFlag;
    }

    public void setCastlingRights(int rights) {
        castlingRights = rights;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public boolean isBlackTurn() {
        return !whiteTurn;
    }

    public void toggleTurn() {
        whiteTurn = !whiteTurn;
    }

    public void setTurn(boolean isWhiteTurn) {
        whiteTurn = isWhiteTurn;
    }

    public void setInCheck(PieceColourType kingColor, boolean value) {
        if (kingColor == PieceColourType.WHITE) {
            isCheck = (isCheck & ~1) | (value ? 1 : 0);
        } else {
            isCheck = (isCheck & ~2) | (value ? 2 : 0);
        }
    }

    public boolean isInCheck(PieceColourType kingColor) {
        if (kingColor == PieceColourType.WHITE) {
            return (isCheck & 1) == 1;
        } else {
            return (isCheck & 2) == 2;
        }
    }

    public boolean isInCheckmate() {
        return (isCheckMate & 0b11) != 0; // Either king in checkmate
    }

    public boolean isInCheckmate(PieceColourType kingColor) {
        if (kingColor == PieceColourType.WHITE) {
            return (isCheckMate & 1) == 1;
        } else {
            return (isCheckMate & 2) == 2;
        }
    }

    public void setInCheckmate(boolean value) {
        if (value) {
            isCheckMate |= whiteTurn ? 2 : 1; // Set the bit for the current player
        } else {
            isCheckMate = 0; // Clear checkmate state
        }
    }

    public void setInCheckmate(PieceColourType kingColor, boolean value) {
        if (kingColor == PieceColourType.WHITE) {
            isCheckMate = (isCheckMate & ~1) | (value ? 1 : 0);
        } else {
            isCheckMate = (isCheckMate & ~2) | (value ? 2 : 0);
        }
    }

    public PieceColourType getCurrentPlayerColor() {
        return whiteTurn ? PieceColourType.WHITE : PieceColourType.BLACK;
    }

    public char[][] getBoardMatrix() {
        char[][] matrix = new char[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                matrix[row][col] = ' ';
            }
        }

        for (Map.Entry<Piece, Long> entry : boards.entrySet()) {
            Piece piece = entry.getKey();
            long bitboard = entry.getValue();

            char pieceChar = getPieceChar(piece);

            long bb = bitboard;
            while (bb != 0) {
                int index = Long.numberOfTrailingZeros(bb);

                int rank = 7 - (index / 8);
                int file = index % 8;

                matrix[rank][file] = pieceChar;
                bb &= ~(1L << index);
            }
        }

        return matrix;
    }

    public String getBoardMatrixString() {
        char[][] matrix = getBoardMatrix();
        StringBuilder sb = new StringBuilder();

        sb.append("  a b c d e f g h\n");
        for (int row = 0; row < 8; row++) {
            sb.append(8 - row).append(" ");
            for (int col = 0; col < 8; col++) {
                sb.append(matrix[row][col]).append(" ");
            }
            sb.append("\n");
        }

        // Add status information
        sb.append("\nCurrent turn: ").append(whiteTurn ? "White" : "Black");

        if (isInCheck(PieceColourType.WHITE)) {
            sb.append("\nWhite king is in CHECK!");
        }
        if (isInCheck(PieceColourType.BLACK)) {
            sb.append("\nBlack king is in CHECK!");
        }

        if (isInCheckmate(PieceColourType.WHITE)) {
            sb.append("\nCHECKMATE! Black wins!");
        } else if (isInCheckmate(PieceColourType.BLACK)) {
            sb.append("\nCHECKMATE! White wins!");
        }

        // Add castling rights information
        sb.append("\nCastling rights: ");
        if (hasCastlingRight(WHITE_KINGSIDE)) sb.append("K");
        if (hasCastlingRight(WHITE_QUEENSIDE)) sb.append("Q");
        if (hasCastlingRight(BLACK_KINGSIDE)) sb.append("k");
        if (hasCastlingRight(BLACK_QUEENSIDE)) sb.append("q");
        if (castlingRights == 0) sb.append("none");

        return sb.toString();
    }

    private char getPieceChar(Piece piece) {
        char pieceChar;

        switch (piece.getType()) {
            case PAWN:
                pieceChar = 'P';
                break;
            case KNIGHT:
                pieceChar = 'N';
                break;
            case BISHOP:
                pieceChar = 'B';
                break;
            case ROOK:
                pieceChar = 'R';
                break;
            case QUEEN:
                pieceChar = 'Q';
                break;
            case KING:
                pieceChar = 'K';
                break;
            default:
                return '?';
        }

        return (piece.getColor() == PieceColourType.BLACK) ?
                Character.toLowerCase(pieceChar) : pieceChar;
    }
}