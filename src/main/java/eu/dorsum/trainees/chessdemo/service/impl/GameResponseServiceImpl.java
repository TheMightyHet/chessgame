package eu.dorsum.trainees.chessdemo.service.impl;

import eu.dorsum.trainees.chessdemo.model.GameResponse;
import eu.dorsum.trainees.chessdemo.service.GameResponseService;
import org.springframework.stereotype.Service;

@Service
public class GameResponseServiceImpl implements GameResponseService {

    @Override
    public GameResponse getGameResponse(Long id){
        GameResponse gameresponse= new GameResponse();
        return gameresponse;
    }
}
