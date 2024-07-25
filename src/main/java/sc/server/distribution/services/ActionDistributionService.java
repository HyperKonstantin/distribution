package sc.server.distribution.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sc.server.distribution.repositories.CurrencyRepository;
import sc.server.distribution.repositories.ServerRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActionDistributionService {

    private final int PING_COUNT_TO_CONFIRM = 3;

    private final ServerRepository serverRepository;
    private final CurrencyService currencyService;
    private final CurrencyRepository currencyRepository;
    private final ServerAddingService serverAddingService;
    private final ServerRemovalService serverRemovalService;

    public void checkServers(String pingMessage){

        var aliveServersPingCount = serverRepository.getAliveServersPingCount();
        int serverCount = serverRepository.getServerCount();

        if (aliveServersPingCount.containsKey(pingMessage)){
            aliveServersPingCount.compute(pingMessage, (k, pingCount) -> pingCount + 1);
        }
        else{
            aliveServersPingCount.put(pingMessage, 1);
        }

        if (aliveServersPingCount.values().stream().allMatch(value -> value < PING_COUNT_TO_CONFIRM)){
            return;
        }

        log.info("Servers count: {}", serverCount);

        //TODO simplify
        if (aliveServersPingCount.size() < serverCount ||
                (aliveServersPingCount.size() == 1 && !currencyService.isServerProcessAllCurrencies())){
            serverRemovalService.setServerWasDeleted();
            serverRepository.setServerCount(aliveServersPingCount.size());
        }
        else if (!serverRemovalService.isServerWasDeleted()){
            serverRepository.setServerCount(aliveServersPingCount.size());
        }

        if (serverRemovalService.isServerWasDeleted()){
            serverRemovalService.sendServerState();
        }
        else if (isLackOfCurrency()) {
            serverAddingService.offerRequest();
        }
        else if (isExcessOfCurrency()) {
            serverAddingService.sendOverflowMessage();
        }

        aliveServersPingCount.clear();
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
        return currencyRepository.findAll().size() / serverRepository.getServerCount();
    }
}