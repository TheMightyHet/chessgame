package eu.dorsum.trainees.chessdemo.model.board;

import java.util.HashMap;
import java.util.Map;

public class BitBoard {
    HashMap<Piece, Long> boards;
    private StateManager stateManager;
    private int castlingRights;
    private boolean whiteTurn;
    private int enPassant;
    private int isCheck;
    private int isCheckMate;


    public BitBoard() {
        stateManager = new StateManager();
        boards = new HashMap<>();
        castlingRights = 0b1111;
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
    }


    public long getBitBoard(Piece pieceType) {
        return boards.get(pieceType);
    }

    public int getCastlingRights() {
        return castlingRights;
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

    public boolean isInCheckmate() {
        return (isCheckMate & 1) == 1;
    }

    public void setInCheckmate(boolean value) {
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