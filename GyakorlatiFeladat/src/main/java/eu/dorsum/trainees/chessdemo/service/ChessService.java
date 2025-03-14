package eu.dorsum.trainees.chessdemo.service;
import eu.dorsum.trainees.chessdemo.model.board.FENConverter;
import eu.dorsum.trainees.chessdemo.model.board.Piece;
import eu.dorsum.trainees.chessdemo.model.board.PieceType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChessService {
    private final FENConverter chessBoard;

    public ChessService() {
        this.chessBoard = new FENConverter();
    }

    public void applyMove(String notation) {
        chessBoard.applyMove(notation);
    }

    public Map<Piece, String> getBitboards() {
        return chessBoard.getBitboards();
    }
}