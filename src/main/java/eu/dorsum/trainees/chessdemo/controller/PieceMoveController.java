package eu.dorsum.trainees.chessdemo.controller;

import eu.dorsum.trainees.chessdemo.model.board.Piece;
import eu.dorsum.trainees.chessdemo.model.board.PieceColourType;
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

            // Add information about checks
            PieceColourType currentTurn = pieceMoveService.getCurrentTurn();
            response.put("currentTurn", currentTurn.toString());
            response.put("whiteInCheck", pieceMoveService.isInCheck(PieceColourType.WHITE));
            response.put("blackInCheck", pieceMoveService.isInCheck(PieceColourType.BLACK));

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
            // Get updated game state
            PieceColourType currentTurn = pieceMoveService.getCurrentTurn();
            boolean whiteInCheck = pieceMoveService.isInCheck(PieceColourType.WHITE);
            boolean blackInCheck = pieceMoveService.isInCheck(PieceColourType.BLACK);
            boolean whiteInCheckmate = pieceMoveService.isInCheckmate(PieceColourType.WHITE);
            boolean blackInCheckmate = pieceMoveService.isInCheckmate(PieceColourType.BLACK);

            response.put("currentTurn", currentTurn.toString());
            response.put("whiteInCheck", whiteInCheck);
            response.put("blackInCheck", blackInCheck);
            response.put("whiteInCheckmate", whiteInCheckmate);
            response.put("blackInCheckmate", blackInCheckmate);

            // Determine game state message
            if (whiteInCheckmate) {
                response.put("gameState", "CHECKMATE - Black wins!");
            } else if (blackInCheckmate) {
                response.put("gameState", "CHECKMATE - White wins!");
            } else if (whiteInCheck) {
                response.put("gameState", "White king is in check!");
            } else if (blackInCheck) {
                response.put("gameState", "Black king is in check!");
            } else {
                response.put("gameState", "Game in progress");
            }

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

        // Get all piece positions
        for (Piece piece : Piece.values()) {
            List<String> positions = pieceMoveService.getPiecePositions(piece);
            if (!positions.isEmpty()) {
                piecePositions.put(piece.toString(), positions);
            }
        }

        // Get current game state
        PieceColourType currentTurn = pieceMoveService.getCurrentTurn();
        boolean whiteInCheck = pieceMoveService.isInCheck(PieceColourType.WHITE);
        boolean blackInCheck = pieceMoveService.isInCheck(PieceColourType.BLACK);
        boolean whiteInCheckmate = pieceMoveService.isInCheckmate(PieceColourType.WHITE);
        boolean blackInCheckmate = pieceMoveService.isInCheckmate(PieceColourType.BLACK);

        response.put("pieces", piecePositions);
        response.put("matrix", pieceMoveService.getBoardMatrix());
        response.put("currentTurn", currentTurn.toString());
        response.put("whiteInCheck", whiteInCheck);
        response.put("blackInCheck", blackInCheck);
        response.put("whiteInCheckmate", whiteInCheckmate);
        response.put("blackInCheckmate", blackInCheckmate);

        // Add game state message
        if (whiteInCheckmate) {
            response.put("gameState", "CHECKMATE - Black wins!");
        } else if (blackInCheckmate) {
            response.put("gameState", "CHECKMATE - White wins!");
        } else if (whiteInCheck) {
            response.put("gameState", "White king is in check!");
        } else if (blackInCheck) {
            response.put("gameState", "Black king is in check!");
        } else {
            response.put("gameState", "Game in progress");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/boardString")
    public ResponseEntity<Map<String, Object>> getBoardString() {
        Map<String, Object> response = new HashMap<>();

        response.put("board", pieceMoveService.getBoardMatrixString());
        response.put("currentTurn", pieceMoveService.getCurrentTurn().toString());
        response.put("whiteInCheck", pieceMoveService.isInCheck(PieceColourType.WHITE));
        response.put("blackInCheck", pieceMoveService.isInCheck(PieceColourType.BLACK));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{color}")
    public ResponseEntity<Map<String, Object>> isInCheck(@PathVariable String color) {
        Map<String, Object> response = new HashMap<>();

        try {
            PieceColourType pieceColor = PieceColourType.valueOf(color.toUpperCase());
            boolean isInCheck = pieceMoveService.isInCheck(pieceColor);
            boolean isInCheckmate = pieceMoveService.isInCheckmate(pieceColor);

            response.put("color", color);
            response.put("inCheck", isInCheck);
            response.put("inCheckmate", isInCheckmate);

            if (isInCheckmate) {
                response.put("gameState", "CHECKMATE - " +
                        (pieceColor == PieceColourType.WHITE ? "Black" : "White") + " wins!");
            } else if (isInCheck) {
                response.put("gameState", color + " king is in check!");
            } else {
                response.put("gameState", "Game in progress");
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", "Invalid color: " + color);
            response.put("message", "Color must be 'WHITE' or 'BLACK'");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/checkmate")
    public ResponseEntity<Map<String, Object>> getCheckmateStatus() {
        Map<String, Object> response = new HashMap<>();

        boolean whiteInCheckmate = pieceMoveService.isInCheckmate(PieceColourType.WHITE);
        boolean blackInCheckmate = pieceMoveService.isInCheckmate(PieceColourType.BLACK);

        response.put("whiteInCheckmate", whiteInCheckmate);
        response.put("blackInCheckmate", blackInCheckmate);

        if (whiteInCheckmate) {
            response.put("gameState", "CHECKMATE - Black wins!");
            response.put("gameOver", true);
            response.put("winner", "BLACK");
        } else if (blackInCheckmate) {
            response.put("gameState", "CHECKMATE - White wins!");
            response.put("gameOver", true);
            response.put("winner", "WHITE");
        } else {
            response.put("gameState", "Game in progress");
            response.put("gameOver", false);
        }

        return ResponseEntity.ok(response);
    }
}