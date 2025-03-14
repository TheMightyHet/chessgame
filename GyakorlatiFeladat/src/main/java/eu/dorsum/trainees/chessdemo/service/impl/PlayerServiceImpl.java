package eu.dorsum.trainees.chessdemo.service.impl;

import eu.dorsum.trainees.chessdemo.dao.PlayerDao;
import eu.dorsum.trainees.chessdemo.model.Player;
import eu.dorsum.trainees.chessdemo.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerServiceImpl.class);
    private final PlayerDao playerdao;

    public PlayerServiceImpl(PlayerDao playerdao) {
        this.playerdao = playerdao;
    }

    @Override
    public List<Player> getPlayers() {
        LOG.info("getPlayers started");
        return playerdao.getPlayers();
    }

    @Override
    public String addPlayer(String username) {
        LOG.info("addPlayers started");
        playerdao.addPlayer(username);
        return username;
    }

    @Override
    public Player getPlayerByUsername(String username) {
        LOG.info("getPlayersByUsername started");
        return playerdao.getPlayerByUsername(username);
    }
}
