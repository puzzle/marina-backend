package ch.puzzle.marinabackend.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationInfoResource {

    @GetMapping("/applicationinfo")
    public ApplicationInfo getApplicationInfo() {
        return new ApplicationInfo();
    }
}
