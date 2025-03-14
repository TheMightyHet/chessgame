package eu.dorsum.trainees.chessdemo.model.board;

public class BitBoard implements Board{
    private long whitePieces[];
    private long blackPieces[];
    private StateManager stateManager;
    private int castlingRights;

    public BitBoard() {
        stateManager = new StateManager();
        whitePieces = new long[6];
        blackPieces = new long[6];
        castlingRights = 0b1111;
    }

    public void initBoard(String boardData){

        whitePieces[pawn] = stateManager.getPiecesOfType(boardData, true, pawn);
        blackPieces[pawn] = stateManager.getPiecesOfType(boardData, false, pawn);

        whitePieces[bishop] = stateManager.getPiecesOfType(boardData, true, bishop);
        blackPieces[bishop] = stateManager.getPiecesOfType(boardData, false, bishop);

        whitePieces[knight] = stateManager.getPiecesOfType(boardData, true, knight);
        blackPieces[knight] = stateManager.getPiecesOfType(boardData, false, knight);

        whitePieces[rook] = stateManager.getPiecesOfType(boardData, true, rook);
        blackPieces[rook] = stateManager.getPiecesOfType(boardData, false, rook);

        whitePieces[queen] = stateManager.getPiecesOfType(boardData, true, queen);
        blackPieces[queen] = stateManager.getPiecesOfType(boardData, false, queen);

        whitePieces[king] = stateManager.getPiecesOfType(boardData, true, king);
        blackPieces[king] = stateManager.getPiecesOfType(boardData, false, king);

    }

    public long getBitBoard(boolean isWhite, int pieceType){
        if(isWhite){
            return whitePieces[pieceType];
        }else{
            return blackPieces[pieceType];
        }
    }


}
