package sc.server.distribution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
* Ошибочные сценарии:
*	1. в пустой сети создаются несколько серверов
* 	2. удаление одного сервера одновременно с созданием другого
*
*
*
*
*
*/

@SpringBootApplication
// mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=8081' -Dspring-boot.run.arguments=--values.server-id=1
public class DistributionApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributionApplication.class, args);
	}

}
