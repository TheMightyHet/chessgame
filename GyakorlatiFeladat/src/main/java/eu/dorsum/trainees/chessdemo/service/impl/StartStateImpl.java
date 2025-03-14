package eu.dorsum.trainees.chessdemo.service.impl;

import eu.dorsum.trainees.chessdemo.dao.StartStateDao;
import eu.dorsum.trainees.chessdemo.model.StartState;
import eu.dorsum.trainees.chessdemo.service.StartStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StartStateImpl implements StartStateService {

    private static final Logger LOG = LoggerFactory.getLogger(StartStateImpl.class);
    private final StartStateDao startStateDao;

    public StartStateImpl(StartStateDao startStateDao) {
        this.startStateDao = startStateDao;
    }

    @Override
    public List<StartState> getStateService() {
        LOG.info("getStartState started");
        return startStateDao.getStartState();
    }
}
