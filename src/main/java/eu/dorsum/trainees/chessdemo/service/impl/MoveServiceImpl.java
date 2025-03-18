package eu.dorsum.trainees.chessdemo.service.impl;

import eu.dorsum.trainees.chessdemo.dao.MoveDao;
import eu.dorsum.trainees.chessdemo.model.Move;
import eu.dorsum.trainees.chessdemo.service.MoveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoveServiceImpl implements MoveService {
    private static final Logger LOG = LoggerFactory.getLogger(MoveServiceImpl.class);
    private final MoveDao moveDao;

    public MoveServiceImpl(MoveDao moveDao) {
        this.moveDao = moveDao;
    }

    @Override
    public void addMove(Move move) {
        LOG.info("addMove started");
        moveDao.addMove(move);
    }

    @Override
    public List<Move> getMovesOfGameAsc(Long id) {
        LOG.info("getMovesOfGameAsc started");
        return moveDao.getMovesOfGameAsc(id);
    }
}
