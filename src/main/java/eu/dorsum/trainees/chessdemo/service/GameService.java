package eu.dorsum.trainees.chessdemo.service;

import eu.dorsum.trainees.chessdemo.model.Game;

import java.util.List;

public interface GameService {
    List<Game> getGames();
    Game getGameById(Long id);
    Game addGame(Long whiteId, Long blackId, Long startStateId, String gametypeName);
}
