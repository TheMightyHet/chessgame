package eu.dorsum.trainees.chessdemo.service.impl;

import eu.dorsum.trainees.chessdemo.model.board.BitBoard;
import eu.dorsum.trainees.chessdemo.service.Chess;
import org.springframework.stereotype.Service;

@Service
public class ChessServiceImpl {

    Chess chess;

    public BitBoard getBoardByID(long gameID) {
        return chess.getBoardsByGameID(gameID);
    }
}
