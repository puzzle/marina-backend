package ch.puzzle.marinabackend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MarinaBackendApplicationMainTests {

    @Test
    public void contextLoads() {
        String[] args = {"--spring.config.location=classpath:/application-test.yml", "--server.port=8099"};
        MarinaBackendApplication.main(args);
    }
}
