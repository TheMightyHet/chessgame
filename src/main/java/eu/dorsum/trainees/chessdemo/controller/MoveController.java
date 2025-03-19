package eu.dorsum.trainees.chessdemo.controller;

import eu.dorsum.trainees.chessdemo.model.Move;
import eu.dorsum.trainees.chessdemo.service.MoveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("move")
@RestController
public class MoveController {
    private final MoveService moveService;

    public MoveController(MoveService moveService) {
        this.moveService = moveService;
    }

    @GetMapping("getMovesOfGameAscById/{id}")
    public List<Move> getMovesOfGameAscById(@PathVariable Long id) {
        return moveService.getMovesOfGameAsc(id);
    }

    @PostMapping("addMove")
    public ResponseEntity<Move> addMove(@RequestBody Move move){
        moveService.addMove(move);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
