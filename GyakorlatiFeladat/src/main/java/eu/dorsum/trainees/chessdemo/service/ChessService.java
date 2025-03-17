package eu.dorsum.trainees.chessdemo.service;

import eu.dorsum.trainees.chessdemo.model.board.FENConverter;
import eu.dorsum.trainees.chessdemo.model.board.NotationConverter;
import eu.dorsum.trainees.chessdemo.model.board.Piece;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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

    public Map<Piece, Long> getBitboards() {
        return chessBoard.getBitboards();
    }

    public Map<String, String> getBitboardsAsNotation() {
        Map<String, String> result = new HashMap<>();
        Map<Piece, Long> bitboards = chessBoard.getBitboards();

        for (Map.Entry<Piece, Long> entry : bitboards.entrySet()) {
            List<String> cellNotations = NotationConverter.bitboardToCellNotations(entry.getValue());
            result.put(entry.getKey().toString(), String.join(",", cellNotations));
        }

        return result;
    }
}