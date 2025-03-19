package eu.dorsum.trainees.chessdemo.service;

import eu.dorsum.trainees.chessdemo.model.Move;

import java.util.List;

public interface MoveService {
    void addMove(Move move);
    List<Move> getMovesOfGameAsc(Long id);
}
