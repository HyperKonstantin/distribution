package sc.server.distribution.kafka;

import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sc.server.distribution.entities.Currency;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public final String serverId;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, Environment environment){
        this.kafkaTemplate = kafkaTemplate;
        serverId = environment.getProperty("values.server-id");
    }

    public void healthCheck(){
        kafkaTemplate.send("core-balancer", "ping " + serverId);
    }

    public void currencyQuery(){
        kafkaTemplate.send("core-balancer", "query " + serverId);
    }

    public void offerCurrency(Currency currency, String consumerId){
        kafkaTemplate.send("core-balancer", "offer " + serverId + " " + consumerId + " " + currency.getName());
    }
}
