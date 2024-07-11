package sc.server.distribution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistributionApplication {  // mvn spring-boot:run -Dspring-boot.run.arguments=--values.server-id=<id>

	public static void main(String[] args) {
		SpringApplication.run(DistributionApplication.class, args);
	}

}
