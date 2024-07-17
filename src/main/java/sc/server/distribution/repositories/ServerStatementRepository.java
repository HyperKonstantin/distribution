package sc.server.distribution.repositories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sc.server.distribution.kafka.KafkaProducer;
import sc.server.distribution.services.CurrencyService;
import sc.server.distribution.services.OfferManagementService;
import sc.server.distribution.services.RemovalDistributionService;

import java.util.HashMap;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ServerStatementRepository {

    private final int PING_COUNT_TO_CONFIRM = 5;

    private final CurrencyService currencyService;
    private final CurrencyRepository currencyRepository;
    private final OfferManagementService offerManagementService;
    private final KafkaProducer kafkaProducer;
    private final RemovalDistributionService removalDistributionService;

    @Getter
    private int serverCount = 1;
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

            if (serverCount == 1 && kafkaProducer.getServerId().equals("1")){
                log.info("({}) server process all currencies!", kafkaProducer.getServerId());
                currencyService.processAllCurrencies();
            }

            if (aliveServersPingCount.size() < serverCount){
                removalDistributionService.setServerWasDeleted(true);
            }

            serverCount = aliveServersPingCount.size();

            if (removalDistributionService.isServerWasDeleted()){
                removalDistributionService.sendServerState();
            }
            else if (isLackOfCurrency()) {
                offerManagementService.offerRequest();
            }
            else if (isExcessOfCurrency()) {
                offerManagementService.sendOverflowMessage();
            }

            aliveServersPingCount.clear();
        }
    }

    public boolean isLackOfCurrency(){
        return currencyService.getProcessedCurrency().size() < currencyPerServer();
    }

    public boolean isFullnessOrExcessOfCurrency(){
        return currencyService.getProcessedCurrency().size() > currencyPerServer();

    }

    public boolean isExcessOfCurrency(){
        return currencyService.getProcessedCurrency().size() > currencyPerServer() + 1;
    }

    public int currencyPerServer(){
        return currencyRepository.findAll().size() / serverCount;
    }
}