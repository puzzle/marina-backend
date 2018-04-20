package ch.puzzle.marinabackend;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication
@Configuration
@PropertySource("application-test.yml")
public class TestConfiguration {

	@Bean
	protected UserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("read")
				.build();
		UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("password").roles("ADMIN")
				.build();
		return new InMemoryUserDetailsManager(user, admin);
	}
	
}
