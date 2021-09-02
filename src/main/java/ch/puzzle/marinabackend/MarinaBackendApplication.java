package ch.puzzle.marinabackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
//@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MarinaBackendApplication extends WebSecurityConfigurerAdapter {

    @Autowired
    private KeycloakRolesExtractor keycloakRolesExtractor;

    public static void main(String[] args) {
        SpringApplication.run(MarinaBackendApplication.class, args);
    }

    @Bean
    @Order(0)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {
        return new io.sentry.spring.SentryExceptionResolver();
    }

    @Bean
    public ServletContextInitializer sentryServletContextInitializer() {
        return new io.sentry.spring.SentryServletContextInitializer();
    }


    @Value("${application.security.enable-csrf}")
    private boolean csrfEnabled;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (!csrfEnabled) {
            http = http.csrf().disable();
        }
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/swagger-ui/**", "/swagger-resources/**", "/v2/**").permitAll()
                .antMatchers("/", "/login**", "/webjars/**", "/actuator/health", "/actuator/prometheus", "/applicationinfo").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()));
//        .redirectionEndpoint().baseUri();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);
            oidcUser = new DefaultOidcUser(
                    keycloakRolesExtractor.extractAuthorities(oidcUser),
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo()
            );
            return oidcUser;
        };
    }
}
