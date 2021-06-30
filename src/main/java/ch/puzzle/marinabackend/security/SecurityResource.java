package ch.puzzle.marinabackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@RestController
public class SecurityResource {

    @Value("${application.security.redirecturl.frontend}")
    private String projectUrl;

    @Value("${application.security.redirecturl.logout}")
    private String logoutUrl;

    @Autowired
    private SecurityService securityService;

    @GetMapping("/user")
    public User getCurrentUser(Principal principal) {
        return securityService.convertPrincipal(principal);
    }

    @GetMapping("/sso")
    public RedirectView loginSsoRedirectToGui() {
        return new RedirectView(projectUrl);
    }

    @GetMapping("/ssoLogout")
    public RedirectView logoutSsoRedirectToGui(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SecurityContextHolder.clearContext();
        if (session != null) {
            session.invalidate();
        }
        return new RedirectView(logoutUrl);
    }
}
