package eu.dorsum.trainees.chessdemo.controller;

import eu.dorsum.trainees.chessdemo.model.Game;
import eu.dorsum.trainees.chessdemo.model.enums.GameType;
import eu.dorsum.trainees.chessdemo.service.GameService;
import eu.dorsum.trainees.chessdemo.service.PlayerService;
import eu.dorsum.trainees.chessdemo.service.StartStateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RequestMapping("game")
@RestController
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;
    private final StartStateService startStateService;

    public GameController(GameService gameService, PlayerService playerService, StartStateService startStateService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.startStateService = startStateService;
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
    public ResponseEntity<Long>  createGame(@PathVariable Long whiteId,
                                          @PathVariable Long blackId,
                                          @PathVariable Long startStateId,
                                          @PathVariable String gameTypeName){

        if (!whiteId.equals(blackId) &&
                playerService.getPlayerById(whiteId)!=null &&
                playerService.getPlayerById(blackId)!=null &&
                startStateService.getStartStateById(startStateId)!=null &&
                GameType.GAMETYPE_DEFAULT.isValid(gameTypeName)
        ){
            return ResponseEntity.ok().body(gameService.addGame(whiteId,blackId,startStateId,gameTypeName).getId());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();

    }
}
