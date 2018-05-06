package ch.puzzle.marinabackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

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

    @RequestMapping(path = "/sso", method = RequestMethod.GET)
    public RedirectView loginSsoRedirectToGui() {
        return new RedirectView(projectUrl);
    }
}
