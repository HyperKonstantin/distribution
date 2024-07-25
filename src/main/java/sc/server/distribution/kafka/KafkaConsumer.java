package sc.server.distribution.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;
import sc.server.distribution.services.ActionDistributionService;
import sc.server.distribution.services.ServerAddingService;
import sc.server.distribution.services.ServerRemovalService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final ActionDistributionService actionDistributionService;
    private final ServerAddingService serverAddingService;
    private final ServerRemovalService serverRemovalService;

    @KafkaListener(topicPartitions = @TopicPartition(topic = "core-balancer",
            partitions = "#{@finder.partitions('core-balancer')}"))
    public void listen(String message){
        log.info(message);
        if (message.contains("ping")){
            actionDistributionService.checkServers(message);
        }
        //TODO move serverStatement to offerManager
        else if (message.contains("query") && actionDistributionService.isFullnessOrExcessOfCurrency()
        && !serverRemovalService.isServerWasDeleted()){
            String querySentServerId = message.split(" ")[1];
            serverAddingService.sendOfferOnQueryFrom(querySentServerId);
        }
        else if (message.contains("offer")){
            serverAddingService.confirmOffer(message);
        }
        else if (message.contains("state") && serverRemovalService.isServerWasDeleted()){
            serverRemovalService.processState(message);
        }
        else if (message.contains("take")){
            serverRemovalService.takeCurrency(message);
        }
        else if (message.contains("overflow")){
            serverAddingService.forcedQuery(actionDistributionService.currencyPerServer());
        }
    }
}
