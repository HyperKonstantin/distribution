package sc.server.distribution.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sc.server.distribution.entities.Currency;
import sc.server.distribution.kafka.KafkaProducer;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferManagementService {

    private final KafkaProducer kafkaProducer;
    private final DistributionService distributionService;

    private boolean isOfferIntercepted;

    public void sendOffer(Currency currency, String consumerId) {
        kafkaProducer.offerCurrency(currency, consumerId);
        isOfferIntercepted = false;
    }

    public void confirmOffer(String message) {
         String offerServerId = message.split(" ")[1];
         String consumerServerId = message.split(" ")[2];

         if (!offerServerId.equals(kafkaProducer.serverId)){
             isOfferIntercepted = true;
         }

         else if (!isOfferIntercepted){
             String currencyName = message.split(" ")[3];
             distributionService.removeCurrency(currencyName);
         }

         else if (consumerServerId.equals(kafkaProducer.serverId)){
             //TODO
         }
    }
}
