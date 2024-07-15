package sc.server.distribution.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sc.server.distribution.repositories.ServersStatementRepository;
import sc.server.distribution.services.OfferManagementService;

@Service
@RequiredArgsConstructor
@KafkaListener(topics = "core-balancer", id = "1")
public class KafkaConsumer {

    private final ServersStatementRepository serversStatementRepository;
    private final OfferManagementService offerManagementService;

    @KafkaHandler
    public void listen(String message){
        if (message.contains("ping")){
            serversStatementRepository.addServer(message);
        }
        else if (message.contains("query") && serversStatementRepository.isExcessOfCurrency()){
            String querySentServerId = message.split(" ")[1];
            offerManagementService.sendOfferOnQueryFrom(querySentServerId);
        }
        else if (message.contains("offer")){
            offerManagementService.confirmOffer(message);
        }
    }
}
