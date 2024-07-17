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

    public void sendOverflowMessage() {
        log.info("({}) server is overflow", kafkaProducer.getServerId());
        kafkaProducer.OverflowMessage();
    }

    public void offerRequest() {
        if (offerStatus == OfferStatus.waited){
            log.warn("({}) query was not answered, send another", kafkaProducer.getServerId());
        }
        log.info("send query");
        kafkaProducer.currencyQuery();
        offerStatus = OfferStatus.waited;
    }

    public void forcedQuery(int currencyPerServer) {
        if (currencyService.getProcessedCurrency().size() == currencyPerServer){
            log.info("({}) sending forced query", kafkaProducer.getServerId());
            offerRequest();
        }
    }

    private enum OfferStatus{
        sent, none, waited
    }

    public void sendOfferOnQueryFrom(String serverId) {
        if (offerStatus == OfferStatus.sent){
            log.info("({}) Server is already sent offer", kafkaProducer.getServerId());
            return;
        }

        List<Currency> processedCurrency = currencyService.getProcessedCurrency();
        Currency offeredCurrency = processedCurrency.get(0);

        log.info("({}) send offer to {} on {}, pc: {}", kafkaProducer.getServerId(), serverId, offeredCurrency.getName(), processedCurrency.size());

        kafkaProducer.offerCurrency(offeredCurrency, serverId);
        offerStatus = OfferStatus.sent;
    }

    public void confirmOffer(String message) {
        
        if (isOfferAnswered(message)){
            log.info("({}) query was answered by {}", kafkaProducer.getServerId(), OfferSender(message));
            currencyService.addCurrency(currencyName(message));
            offerStatus = OfferStatus.none;

        }
        else if (isOfferFailured(message)){
         log.info("({}) offer was intercepted", kafkaProducer.getServerId());
         offerStatus = OfferStatus.none;
        }
         
        else if (isOfferSucceed(message)) {
         log.info("({}) offer was confirmed", kafkaProducer.getServerId());
         currencyService.removeCurrency(currencyName(message));
         offerStatus = OfferStatus.none;
        }
    }

    private boolean isOfferSucceed(String message){
        return OfferSender(message).equals(kafkaProducer.getServerId()) && offerStatus == OfferStatus.sent;
    }

    private boolean isOfferFailured(String message){
        return !OfferSender(message).equals(kafkaProducer.getServerId()) && offerStatus == OfferStatus.sent;
    }

    private boolean isOfferAnswered(String message){
        return OfferConsumer(message).equals(kafkaProducer.getServerId()) && offerStatus == OfferStatus.waited;
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
