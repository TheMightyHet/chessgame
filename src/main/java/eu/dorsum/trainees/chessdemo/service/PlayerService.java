package eu.dorsum.trainees.chessdemo.service;

import eu.dorsum.trainees.chessdemo.model.Player;

import java.util.List;

public interface PlayerService {

    List<Player> getPlayers();
    String addPlayer(String username);
    Player getPlayerByUsername(String username);
    Player getPlayerById(Long id);

}
