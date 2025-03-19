package eu.dorsum.trainees.chessdemo.controller;

import eu.dorsum.trainees.chessdemo.model.board.MoveManager;
import eu.dorsum.trainees.chessdemo.model.board.Piece;
import eu.dorsum.trainees.chessdemo.service.impl.PieceMoveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/chess")
public class PieceMoveController {

    private final PieceMoveService pieceMoveService;

    @Autowired
    public PieceMoveController(PieceMoveService pieceMoveService) {
        this.pieceMoveService = pieceMoveService;
    }

    @GetMapping("/moves/{position}")
    public ResponseEntity<Map<String, Object>> getPossibleMoves(@PathVariable String position) {
        try {
            List<String> moves = pieceMoveService.getPossibleMoves(position);
            List<String> notations = pieceMoveService.getPossibleMovesWithNotation(position);

            Map<String, Object> response = new HashMap<>();
            response.put("position", position);
            response.put("possibleMoves", moves);
            response.put("moveNotations", notations);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid position: " + position);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/move")
    public ResponseEntity<Map<String, Object>> applyMove(@RequestParam String move) {
        Map<String, Object> response = new HashMap<>();

        boolean success = pieceMoveService.applyMove(move);
        response.put("move", move);
        response.put("success", success);

        if (success) {
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid move");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/board")
    public ResponseEntity<Map<String, Object>> getBoardState() {
        Map<String, Object> response = new HashMap<>();
        Map<String, List<String>> piecePositions = new HashMap<>();

        for (Piece piece : Piece.values()) {
            List<String> positions = pieceMoveService.getPiecePositions(piece);
            if (!positions.isEmpty()) {
                piecePositions.put(piece.toString(), positions);
            }
        }

        response.put("pieces", piecePositions);

        char[][] boardMatrix = pieceMoveService.getBoardMatrix();
        response.put("matrix", boardMatrix);
        return ResponseEntity.ok(response);
    }
}