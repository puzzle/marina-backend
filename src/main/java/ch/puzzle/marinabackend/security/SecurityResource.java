package ch.puzzle.marinabackend.security;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class SecurityResource {
    
    @Value("${security.redirecturl.frontend}")
    private String projectUrl;


    @GetMapping("/user")
    public User getCurrentUser(Principal principal) {
        OAuth2Authentication auth = (OAuth2Authentication) principal;
        return new User(auth);
    }
    
    @RequestMapping(path = "/sso")
    public RedirectView loginSsoRedirectToGui(HttpServletResponse httpServletResponse) {
        return new RedirectView(projectUrl);
    }
}
