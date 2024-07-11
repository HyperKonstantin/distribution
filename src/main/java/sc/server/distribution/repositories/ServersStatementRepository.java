package sc.server.distribution.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sc.server.distribution.services.DistributionService;

import java.util.HashMap;

@Repository
@Slf4j
public class ServersStatementRepository {

    private final int PING_COUNT_TO_CONFIRM = 5;

    @Autowired
    private DistributionService distributionService;

    private int serversCount = 0;
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

            if (aliveServersPingCount.size() > serversCount){
                serversCount = aliveServersPingCount.size();
                log.info("New server! Count: {}", serversCount);
                distributionService.distributeWithIncreasingServerCount(serversCount);

            }

            aliveServersPingCount.clear();
        }
    }
}