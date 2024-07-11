package sc.server.distribution.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sc.server.distribution.repositories.ServersStatementRepository;
import sc.server.distribution.services.DistributionService;
import sc.server.distribution.services.OfferManagementService;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ServersStatementRepository serversStatementRepository;
    private final DistributionService distributionService;
    private final OfferManagementService offerManagementService;

    @KafkaListener(topics = "core-balancer", groupId = "3")
    public void listen(String message){
        if (message.contains("ping")){
            serversStatementRepository.addServer(message);
        }
        else if (message.contains("query")){
            distributionService.answerOnQueryFrom(message.split(" ")[1]);
        }
        else if (message.contains("offer")){
            offerManagementService.confirmOffer(message);
        }
    }
}
