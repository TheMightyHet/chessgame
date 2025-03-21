package eu.dorsum.trainees.chessdemo.service;

import eu.dorsum.trainees.chessdemo.model.StartState;

import java.util.List;

public interface StartStateService {
    List<StartState> getStartState();
    StartState getStartStateById(Long id);
}
