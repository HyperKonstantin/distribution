package sc.server.distribution.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sc.server.distribution.entities.Currency;
import sc.server.distribution.kafka.KafkaProducer;
import sc.server.distribution.repositories.CurrencyRepository;
import sc.server.distribution.repositories.ServersStatementRepository;

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

    private final KafkaProducer kafkaProducer;
    private final CurrencyService currencyService;
    private final CurrencyRepository currencyRepository;
    private final ServersStatementRepository serversStatementRepository;

    public void sendServerState() {
        List<String> processedCurrencyNames = currencyService.getProcessedCurrency().stream().map(Currency::getName).toList();
        log.info("({}} state sent: {}", kafkaProducer.getServerId(), processedCurrencyNames);
        kafkaProducer.sendState(String.join(" ", processedCurrencyNames));
    }

    public void processState(String message) {
        String stateSender = message.split(" ")[1];
        List<String> senderProcessedCurrencyNames = Arrays.stream(message.split(" ")).skip(2).toList();
        sentStateServers.put(stateSender, senderProcessedCurrencyNames);

        if (sentStateServers.size() != serversStatementRepository.getServerCount()) {
            return;
        }

        if (getUnprocessedCurrencies().isEmpty()) {
            log.info("({}) Currency distributed!");
            serverWasDeleted = false;
        }
        else{
            log.info("({}) Send take request!");
            SendTakeRequest();
        }

        sentStateServers.clear();

    }

    private void SendTakeRequest() {
        int takenCurrencyIndex = nextInt(0, getUnprocessedCurrencies().size());
        kafkaProducer.takeRequest(getUnprocessedCurrencies().get(takenCurrencyIndex));
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
}
