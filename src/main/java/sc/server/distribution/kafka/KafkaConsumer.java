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

    @KafkaListener(topicPartitions = @TopicPartition(topic = "command-ping",
            partitions = "#{@finder.partitions('command-ping')}"))
    public void pingListen(String message) {
        actionDistributionService.updateServer(message);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "command-query",
            partitions = "#{@finder.partitions('command-query')}"))
    public void queryListen(String message) {
        if (actionDistributionService.isFullnessOrExcessOfCurrency()
                && !serverRemovalService.isServerWasDeleted()){
            String querySentServerId = message.split(" ")[1];
            serverAddingService.sendOfferOnQueryFrom(querySentServerId);
        }
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "command-offer",
            partitions = "#{@finder.partitions('command-offer')}"))
    public void offerListen(String message) {
        serverAddingService.confirmOffer(message);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "command-state",
            partitions = "#{@finder.partitions('command-state')}"))
    public void stateListen(String message) {
        serverRemovalService.processState(message);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "command-take",
            partitions = "#{@finder.partitions('command-take')}"))
    public void takeListen(String message) {
        serverRemovalService.takeCurrency(message);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "command-overflow",
            partitions = "#{@finder.partitions('command-overflow')}"))
    public void overflowListen(String message) {
        serverAddingService.forcedQuery(actionDistributionService.currencyPerServer());
    }
}
