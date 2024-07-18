package sc.server.distribution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
* Баги:
* 	1. (2 сервера по 5 объектов) При добавлении сервера один из серверов кидает overflow,
* 	   а у второго изменения не отобразились. Он, имея 5 объектов попадает под условие processCur == all/servCount
* 	   и бросает forced query.
* 	Итог:
* 	   состояние серверов меняется из (5, 5) на (4, 6), что не критично, но является лишним действием.
*
*	2. (1 сервер, 10 объектов) При добавлении 4 серверов одновременно происходит сбой логики
* 	Итог: Утечка объекта
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
