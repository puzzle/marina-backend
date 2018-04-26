package ch.puzzle.marinabackend.security;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class SecurityResource {
    
    @Value("${security.redirecturl.frontend}")
    private String projectUrl;

    @Autowired
    private SecurityService securityService;

    @GetMapping("/user")
    public User getCurrentUser(Principal principal) {
        return securityService.convertPrincipal(principal);
    }
    
    @GetMapping("/sso")
    public RedirectView loginSsoRedirectToGui(HttpServletResponse httpServletResponse) {
        return new RedirectView(projectUrl);
    }
}
