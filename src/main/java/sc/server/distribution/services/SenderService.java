package sc.server.distribution.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sc.server.distribution.kafka.KafkaProducer;

@Service
public class SenderService {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Scheduled(initialDelay = 5_000, fixedRate = 3_000)
    public void healthCheck(){
        kafkaProducer.healthCheck();
    }
}