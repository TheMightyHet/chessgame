package eu.dorsum.trainees.chessdemo.controller;

import eu.dorsum.trainees.chessdemo.model.Player;
import eu.dorsum.trainees.chessdemo.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("player")
@RestController
public class PlayerController {
private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("getPlayers")
    public List<Player> getPlayers() {
        return playerService.getPlayers();
    }

    @PostMapping("/addPlayer/{username}")
    public ResponseEntity<Player> addPlayer(@PathVariable String username){
        if (playerService.getPlayerByUsername(username)!=null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        playerService.addPlayer(username);
        Player newPlayer = playerService.getPlayerByUsername(username);
    return ResponseEntity.status(HttpStatus.CREATED).body(newPlayer);}
}
