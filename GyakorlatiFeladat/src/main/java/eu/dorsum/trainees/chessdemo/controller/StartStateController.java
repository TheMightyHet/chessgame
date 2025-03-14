package eu.dorsum.trainees.chessdemo.controller;

import eu.dorsum.trainees.chessdemo.model.StartState;
import eu.dorsum.trainees.chessdemo.service.StartStateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("startState")
@RestController
public class StartStateController {
    private final StartStateService startStateService;

    public StartStateController(StartStateService startStateService) {
        this.startStateService = startStateService;
    }

    @GetMapping("/getStartState")
    public List<StartState> getStartState(){return startStateService.getStateService();};
}
