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

    private int serverCount = 1;
    private HashMap<String, Integer> aliveServersPingCount = new HashMap<>();
}