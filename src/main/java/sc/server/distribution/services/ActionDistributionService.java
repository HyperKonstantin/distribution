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

    private final ServerRepository serverRepository;
    private final CurrencyService currencyService;
    private final CurrencyRepository currencyRepository;
    private final ServerAddingService serverAddingService;
    private final ServerRemovalService serverRemovalService;

    public void updateServer(String pingMessage){

        addPingedServer(pingMessage);

        if (!serverRepository.isServersPingedEnoughToConfirm()){
            return;
        }

        log.info("Servers count: {}", serverRepository.getServerCount());
        updateServerCount();

        if (serverRemovalService.isServerWasDeleted()){
            serverRemovalService.sendServerState();
        }
        else if (isLackOfCurrency()) {
            serverAddingService.offerRequest();
        }
        else if (isExcessOfCurrency()) {
            serverAddingService.sendOverflowMessage();
        }

        serverRepository.clearPingedServers();
    }

    private void addPingedServer(String pingMessage){
        if (serverRepository.isServerPinged(pingMessage)){
            serverRepository.increasePingCounter(pingMessage);
        }
        else{
            serverRepository.addServer(pingMessage);
        }
    }

    private void updateServerCount(){
        if (serverRepository.pingedServersCount() < serverRepository.getServerCount() || isServerAlone()){
            serverRemovalService.setServerWasDeleted();
            serverRepository.setServerCount(serverRepository.pingedServersCount());
        }
        else if (!serverRemovalService.isServerWasDeleted()){
            serverRepository.setServerCount(serverRepository.pingedServersCount());
        }
    }

    private boolean isServerAlone(){
        return serverRepository.getAliveServersPingCount().size() == 1
                && !currencyService.isServerProcessAllCurrencies();
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