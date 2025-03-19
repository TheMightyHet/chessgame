package eu.dorsum.trainees.chessdemo.controller;

import eu.dorsum.trainees.chessdemo.model.GameResponse;
import eu.dorsum.trainees.chessdemo.service.GameResponseService;
import eu.dorsum.trainees.chessdemo.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("gameResponse")
@RestController
public class GameResponseController {
    private final GameService gameService;
    private final GameResponseService gameResponseService;

    public GameResponseController(GameService gameService, GameResponseService gameResponseService) {
        this.gameService = gameService;
        this.gameResponseService = gameResponseService;
    }

    @GetMapping("getMovesOfGameAscById/{id}")
    public ResponseEntity<GameResponse> getMovesOfGameAscById(@PathVariable Long id) {
        if (gameService.getGameById(id)!=null){
            return  ResponseEntity.ok().body(gameResponseService.getGameResponse(id));
        }
        return ResponseEntity.notFound().build();
    }
}
