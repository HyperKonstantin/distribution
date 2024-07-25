package sc.server.distribution.repositories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class ServerRepository {

    private final int PING_COUNT_TO_CONFIRM = 3;

    private int serverCount = 1;
    private HashMap<String, Integer> aliveServersPingCount = new HashMap<>();

    public int pingedServersCount(){
        return aliveServersPingCount.size();
    }

    public void clearPingedServers(){
        aliveServersPingCount.clear();
    }

    public boolean isServerPinged(String pingMessage){
        return aliveServersPingCount.containsKey(pingMessage);
    }

    public void increasePingCounter(String pingMessage){
        aliveServersPingCount.compute(pingMessage, (k, pingCount) -> pingCount + 1);
    }

    public void addServer(String pingMessage){
        aliveServersPingCount.put(pingMessage, 1);
    }

    public boolean isServersPingedEnoughToConfirm(){
        return aliveServersPingCount.values().stream().anyMatch(value -> value > PING_COUNT_TO_CONFIRM);
    }
}