package eu.dorsum.trainees.chessdemo.controller;

import eu.dorsum.trainees.chessdemo.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/test1")
    public int getTest1() {
        return testService.getTest1();
    }

}
