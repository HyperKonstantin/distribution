package sc.server.distribution.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;
import sc.server.distribution.repositories.ServerStatementRepository;
import sc.server.distribution.services.OfferManagementService;
import sc.server.distribution.services.RemovalDistributionService;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ServerStatementRepository serversStatementRepository;
    private final OfferManagementService offerManagementService;
    private final RemovalDistributionService removalDistributionService;

    @KafkaListener(topicPartitions = @TopicPartition(topic = "core-balancer",
            partitions = "#{@finder.partitions('core-balancer')}"))
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
        else if (message.contains("state")){
            removalDistributionService.processState(message, serversStatementRepository.getServerCount());
        }
        else if (message.contains("take")){
            removalDistributionService.takeCurrency(message);
        }
        else if (message.contains("overflow")){
            //TODO add overflow logic
        }
    }
}
