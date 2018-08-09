package ch.puzzle.marinabackend;

import ch.puzzle.marinabackend.security.jwt.JWTConfigurer;
import ch.puzzle.marinabackend.security.jwt.TokenVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MarinaBackendApplication extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(MarinaBackendApplication.class, args);
    }

    private TokenVerifier tokenVerifier;

    public MarinaBackendApplication(TokenVerifier tokenVerifier) {
        this.tokenVerifier = tokenVerifier;
    }

    @Bean
    @Order(0)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Value("${security.enable-csrf}")
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
        http.headers().frameOptions().disable()
                .and()
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/**").permitAll()
                .antMatchers("/", "/login**", "/webjars/**", "/actuator/health", "/applicationinfo").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(securityConfigurerAdapter());

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenVerifier);
    }
}
