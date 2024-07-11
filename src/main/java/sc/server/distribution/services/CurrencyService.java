package sc.server.distribution.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sc.server.distribution.entities.Currency;
import sc.server.distribution.repositories.CurrencyRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Getter
    private List<Currency> processedCurrency = new ArrayList<>();

    @SneakyThrows
    public ResponseEntity<?> getAllCurrencies() {
        List<String> currencyNames = currencyRepository.findAll().stream().map(Currency::getName).toList();
        log.info("currency list size: {}", currencyNames.size());
        return new ResponseEntity<>((new ObjectMapper()).writeValueAsString(currencyNames), HttpStatus.OK);
    }

    public void processAllCurrencies(){
        log.info("1 server process all currencies!");
        processedCurrency.clear();
        processedCurrency.addAll(currencyRepository.findAll());
    }

    @SneakyThrows
    public  ResponseEntity<?> getProcessedCurrencies(){
        List<String> processedCurrenciesNames = processedCurrency.stream().map(Currency::getName).toList();
        return new ResponseEntity<>((new ObjectMapper()).writeValueAsString(processedCurrenciesNames), HttpStatus.OK);
    }

    public void removeCurrency(String currencyName){
        processedCurrency.removeIf(currency -> currency.getName().equals(currencyName));
    }

}
