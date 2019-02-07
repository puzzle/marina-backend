package ch.puzzle.marinabackend.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentryTestResource {

    
    @GetMapping("/sentrytestexception")
    public void testException() {
        throw new RuntimeException("sentry test exception");
    }
    
}
