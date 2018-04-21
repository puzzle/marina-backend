package ch.puzzle.marinabackend.security;

import java.security.Principal;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityResource {

    @GetMapping("/user")
    public User getCurrentUser(Principal principal) {
        OAuth2Authentication auth = (OAuth2Authentication) principal;
        return new User(auth);
    }
}
