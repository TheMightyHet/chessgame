package eu.dorsum.trainees.chessdemo.controller;

import eu.dorsum.trainees.chessdemo.model.Game;
import eu.dorsum.trainees.chessdemo.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("game")
@RestController
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("getGames")
    public List<Game> getGames(){
        return gameService.getGames();
    }

    @GetMapping("getGamesById/{id}")
    public Game getGamesById(@PathVariable Long id){
        return gameService.getGameById(id);
    }

    @PostMapping("createGame/{whiteId}/{blackId}/{startStateId}/{gameTypeName}")
    public Long createGame(@PathVariable Long whiteId,
                           @PathVariable Long blackId,
                           @PathVariable Long startStateId,
                           @PathVariable String gameTypeName){
        return gameService.addGame(whiteId,blackId,startStateId,gameTypeName).getId();
    }
}
