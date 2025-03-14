package eu.dorsum.trainees.chessdemo.controller;
import eu.dorsum.trainees.chessdemo.model.board.Piece;
import eu.dorsum.trainees.chessdemo.model.board.PieceType;
import eu.dorsum.trainees.chessdemo.service.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chess")
public class ChessController {
    private final ChessService chessService;

    @Autowired
    public ChessController(ChessService chessService) {
        this.chessService = chessService;
    }

    @PostMapping("/move")
    public Map<String> applyMove(@RequestBody String notation) {
        chessService.applyMove(notation);
        return chessService.getBitboards();
    }
}
