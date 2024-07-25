package sc.server.distribution.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sc.server.distribution.entities.Currency;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Getter
    @Setter
    @Value("${values.server-id}")
    private String serverId;

    public void healthCheck(){
        kafkaTemplate.send("command-ping", "ping " + serverId);
    }

    public void currencyQuery(){
        kafkaTemplate.send("command-query", "query " + serverId);
    }

    public void offerCurrency(Currency currency, String consumerId){
        kafkaTemplate.send("command-offer", "offer " + serverId + " " + consumerId + " " + currency.getName());
    }

    public void sendState(String allCurrencyString) {
        kafkaTemplate.send("command-state", "state " + serverId + " " + allCurrencyString);
    }

    public void takeRequest(String currencyName) {
        kafkaTemplate.send("command-take", "take " + serverId + " " + currencyName);
    }

    public void OverflowMessage() {
        kafkaTemplate.send("command-overflow", "overflow");
    }
}
