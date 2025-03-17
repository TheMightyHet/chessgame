package eu.dorsum.trainees.chessdemo.controller;

import eu.dorsum.trainees.chessdemo.model.board.Piece;
import eu.dorsum.trainees.chessdemo.service.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public Map<String, Object> applyMove(@RequestBody String notation) {
        chessService.applyMove(notation);

        Map<String, Object> response = new HashMap<>();

        // Add notation representation
        response.put("notations", chessService.getBitboardsAsNotation());

        // Add long bitboard values
        Map<String, String> bitboardLongs = new HashMap<>();
        for (Map.Entry<Piece, Long> entry : chessService.getBitboards().entrySet()) {
            bitboardLongs.put(entry.getKey().toString(), "0x" + Long.toHexString(entry.getValue()).toUpperCase());
        }
        response.put("bitboards", bitboardLongs);

        return response;
    }
}