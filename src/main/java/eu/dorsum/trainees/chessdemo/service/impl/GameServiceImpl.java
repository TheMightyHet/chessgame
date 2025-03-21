package eu.dorsum.trainees.chessdemo.service.impl;

import eu.dorsum.trainees.chessdemo.dao.GameDao;
import eu.dorsum.trainees.chessdemo.model.Game;
import eu.dorsum.trainees.chessdemo.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {
    private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);
    private final GameDao gameDao;

    public GameServiceImpl(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Override
    public List<Game> getGames() {
        return gameDao.getGames();
    }

    @Override
    public Game getGameById(Long id) {
        return gameDao.getGameById(id);
    }

    @Override
    public Game addGame(Long whiteId, Long blackId, Long startStateId, String gametypeName) {
        Game newGame = new Game();
        newGame.setWhiteId(whiteId);
        newGame.setBlackId(blackId);
        newGame.setStartStateId(startStateId);
        newGame.setGameTypeName(gametypeName);
        gameDao.addGame(newGame);
        return newGame;
    }
}
