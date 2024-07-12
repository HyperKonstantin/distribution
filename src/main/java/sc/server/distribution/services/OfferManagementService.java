package sc.server.distribution.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sc.server.distribution.entities.Currency;
import sc.server.distribution.kafka.KafkaProducer;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferManagementService {

    private final KafkaProducer kafkaProducer;
    private final CurrencyService currencyService;

    private OfferStatus offerStatus = OfferStatus.none;

    public void offerRequest() {
        kafkaProducer.currencyQuery();
        offerStatus = OfferStatus.waited;
    }

    private enum OfferStatus{
        sent, none, waited
    }

    public void answerOnQueryFrom(String serverId) {
        List<Currency> processedCurrency = currencyService.getProcessedCurrency();
        log.info("({}) answer on query, pc: {}",kafkaProducer.serverId, processedCurrency.size());

        sendOffer(processedCurrency.get(0), serverId);
    }

    public void sendOffer(Currency currency, String consumerId) {
        log.info("({}) send offer to {} on {}", kafkaProducer.serverId, consumerId, currency.getName());
        kafkaProducer.offerCurrency(currency, consumerId);
        offerStatus = OfferStatus.sent;
    }

    public void confirmOffer(String message) {
        
        if (isOfferAnswered(message)){
            log.info("({}) offer was answered by {}", kafkaProducer.serverId, OfferSender(message));
            currencyService.addCurrency(currencyName(message));
            offerStatus = OfferStatus.none;

        }
         if (isOfferFailured(message)){
             log.info("({}) offer was intercepted", kafkaProducer.serverId);
             offerStatus = OfferStatus.none;
         }
         
         else if (isOfferSucceed(message)) {
             log.info("({}) offer was confirmed", kafkaProducer.serverId);
             currencyService.removeCurrency(currencyName(message));
             offerStatus = OfferStatus.none;
         }
    }

    private boolean isOfferSucceed(String message){
        return OfferSender(message).equals(kafkaProducer.serverId) && offerStatus == OfferStatus.sent;
    }

    private boolean isOfferFailured(String message){
        return !OfferSender(message).equals(kafkaProducer.serverId);
    }

    private boolean isOfferAnswered(String message){
        return OfferConsumer(message).equals(kafkaProducer.serverId) && offerStatus == OfferStatus.waited;
    }

    private String OfferSender(String message){
        return message.split(" ")[1];
    }

    private String OfferConsumer(String message){
        return message.split(" ")[2];
    }

    private String currencyName(String message){
        return message.split(" ")[3];
    }
}
