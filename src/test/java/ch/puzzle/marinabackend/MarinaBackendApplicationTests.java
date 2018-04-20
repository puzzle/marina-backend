package ch.puzzle.marinabackend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MarinaBackendApplication.class,
		TestConfiguration.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MarinaBackendApplicationTests {

	@Test
	public void contextLoads() {
	}

}
