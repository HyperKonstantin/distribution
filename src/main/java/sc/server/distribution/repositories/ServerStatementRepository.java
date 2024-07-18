package sc.server.distribution.repositories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sc.server.distribution.kafka.KafkaProducer;
import sc.server.distribution.services.CurrencyService;
import sc.server.distribution.services.OfferManagementService;
import sc.server.distribution.services.RemovalDistributionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private int serverCount = 0;

    //TODO maybe change on Set
    private List<String> activeServersId = new ArrayList<>();

    public void checkServers(String pingMessage){
        String pingedServerId = pingMessage.split(" ")[1];

        activeServersId.add(pingedServerId);

        if (!pingedServerId.equals(kafkaProducer.getServerId())){
            return;
        }

        log.info("Servers count: {}", activeServersId.size());

        if (activeServersId.size() != serverCount && activeServersId.size() == 1){
            log.info("({}) server process all currencies!", kafkaProducer.getServerId());
            currencyService.processAllCurrencies();
        }

        if (activeServersId.size() < serverCount){
            removalDistributionService.setServerWasDeleted(true);
        }

        serverCount = activeServersId.size();

        if (removalDistributionService.isServerWasDeleted()){
            removalDistributionService.sendServerState();
        }
        else if (isLackOfCurrency()) {
            offerManagementService.offerRequest();
        }
        else if (isExcessOfCurrency()) {
            offerManagementService.sendOverflowMessage();
        }

        activeServersId.clear();
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