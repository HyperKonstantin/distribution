package sc.server.distribution.repositories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sc.server.distribution.kafka.KafkaProducer;
import sc.server.distribution.services.CurrencyService;
import sc.server.distribution.services.OfferManagementService;

import java.util.HashMap;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ServersStatementRepository {

    private final int PING_COUNT_TO_CONFIRM = 5;

    private final CurrencyService currencyService;
    private final CurrencyRepository currencyRepository;
    private final OfferManagementService offerManagementService;
    private final KafkaProducer kafkaProducer;

    @Getter
    private int serverCount;
    private HashMap<String, Integer> aliveServersPingCount = new HashMap<>();

    public void addServer(String pingMessage){

        if (aliveServersPingCount.containsKey(pingMessage)){
            aliveServersPingCount.compute(pingMessage, (k, pingCount) -> pingCount + 1);
        }
        else{
            aliveServersPingCount.put(pingMessage, 1);
        }

        log.info(aliveServersPingCount.toString());

        if (aliveServersPingCount.values().stream().anyMatch(value -> value >= PING_COUNT_TO_CONFIRM)){
            log.info("Servers count: {}", aliveServersPingCount.size());

            if (aliveServersPingCount.size() != serverCount) {
                serverCount = aliveServersPingCount.size();
                log.info("New server! Count: {}", serverCount);
                if (serverCount == 1 && kafkaProducer.serverId.equals("1")){
                    log.info("({}) server process all currencies!", kafkaProducer.serverId);
                    currencyService.processAllCurrencies();
                }
            }

            if (isLackOfCurrency()) {
                offerManagementService.offerRequest();
            }

            aliveServersPingCount.clear();
        }
    }

    public boolean isLackOfCurrency(){
        return currencyService.getProcessedCurrency().size() < currencyPerServer();
    }

    public boolean isExcessOfCurrency(){
        return currencyService.getProcessedCurrency().size() > currencyPerServer();
    }

    public int currencyPerServer(){
        return currencyRepository.findAll().size() / serverCount;
    }
}