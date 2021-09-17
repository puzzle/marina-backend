package ch.puzzle.marinabackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class MarinaBackendApplicationMainTests {

    @Test
    public void contextLoads() {
        String[] args = {"--spring.config.location=classpath:/application-test.yml", "--server.port=8099"};
        MarinaBackendApplication.main(args);
    }
}
