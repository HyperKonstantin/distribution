package sc.server.distribution.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sc.server.distribution.entities.Currency;
import sc.server.distribution.kafka.KafkaProducer;
import sc.server.distribution.repositories.CurrencyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistributionService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyService currencyService;
    private final KafkaProducer kafkaProducer;
    private final OfferManagementService offerManagementService;

    private int currencyPerServer;

    public void distributeWithIncreasingServerCount(int serverCount) {
        if (serverCount == 1){
            currencyService.processAllCurrencies();
        }

        currencyPerServer = currencyRepository.findAll().size() / serverCount;

        if (currencyService.getProcessedCurrency().size() < currencyPerServer){
            kafkaProducer.getCurrencyQuery();
        }
    }

    public void answerOnQueryFrom(String serverId) {
        List<Currency> processedCurrency = currencyService.getProcessedCurrency();
        log.info("({}) answer on query, cps: {}, pc: {}",kafkaProducer.serverId, currencyPerServer, processedCurrency.size());

        if (processedCurrency.size() > currencyPerServer){
            offerManagementService.sendOffer(processedCurrency.get(0), serverId);

        }
    }

    public void addCurrencyFromOffer(String currency) {

    }

    public void removeCurrency(String currency){
        currencyService.removeCurrency(currency);
    }
}
