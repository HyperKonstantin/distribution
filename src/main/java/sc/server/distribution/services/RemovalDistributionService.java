package sc.server.distribution.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sc.server.distribution.entities.Currency;
import sc.server.distribution.kafka.KafkaProducer;
import sc.server.distribution.repositories.CurrencyRepository;

import java.util.*;
import static org.apache.commons.lang3.RandomUtils.nextInt;

@Service
@Slf4j
@RequiredArgsConstructor
public class RemovalDistributionService {

    @Setter
    @Getter
    private boolean serverWasDeleted;

    private HashMap<String, List<String>> sentStateServers = new HashMap<>();
    private String takedRequestCurrency = null;

    private final KafkaProducer kafkaProducer;
    private final CurrencyService currencyService;
    private final CurrencyRepository currencyRepository;

    public void sendServerState() {
        List<String> processedCurrencyNames = currencyService.getProcessedCurrency().stream().map(Currency::getName).toList();
        log.info("({}) state sent: {}", kafkaProducer.getServerId(), processedCurrencyNames);
        kafkaProducer.sendState(String.join(" ", processedCurrencyNames));
    }

    public void processState(String message, int serverCount) {
        String stateSender = message.split(" ")[1];
        List<String> senderProcessedCurrencyNames = Arrays.stream(message.split(" ")).skip(2).toList();
        sentStateServers.put(stateSender, senderProcessedCurrencyNames);

        if (sentStateServers.size() != serverCount) {
            return;
        }

        if (getUnprocessedCurrencies().isEmpty()) {
            log.info("({}) Currency distributed!", kafkaProducer.getServerId());
            serverWasDeleted = false;
        }
        else {
            SendTakeRequest();
        }

        sentStateServers.clear();
    }

    private void SendTakeRequest() {
        //TODO add currency count check

        int takenCurrencyIndex = nextInt(0, getUnprocessedCurrencies().size());
        takedRequestCurrency = getUnprocessedCurrencies().get(takenCurrencyIndex);
        kafkaProducer.takeRequest(takedRequestCurrency);

        log.info("({}) Send take request on {}", kafkaProducer.getServerId(), takedRequestCurrency);

    }

    private List<String> getUnprocessedCurrencies(){
        List<String> allServersProcessedCurrencies = sentStateServers.values().stream()
                .flatMap(List::stream)
                .toList();

        List<String> unprocessedCurrencies = currencyRepository.findAll().stream()
                .map(Currency::getName)
                .filter(currency -> !allServersProcessedCurrencies.contains(currency))
                .toList();

        return unprocessedCurrencies;
    }

    public void takeCurrency(String message) {
        log.info("({}) take query got: {}", kafkaProducer.getServerId(), message);
        String senderId = message.split(" ")[1];
        String currencyName = message.split(" ")[2];


        if (kafkaProducer.getServerId().equals(senderId) && takedRequestCurrency != null){
            currencyService.addCurrency(currencyName);

            log.info("({}) take currency: {}", kafkaProducer.getServerId(), currencyName);
        }
        else if (takedRequestCurrency != null && takedRequestCurrency.equals(currencyName)){
            log.info("({}) take request was intercepted by {}", kafkaProducer.getServerId(), senderId);
            takedRequestCurrency = null;
        }
    }
}
