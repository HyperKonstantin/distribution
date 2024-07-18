package sc.server.distribution.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sc.server.distribution.kafka.KafkaProducer;

@Service
public class SenderService {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Scheduled(initialDelay = 2_000, fixedDelay = 2_000)
    public void healthCheck(){
        kafkaProducer.healthCheck();
    }
}