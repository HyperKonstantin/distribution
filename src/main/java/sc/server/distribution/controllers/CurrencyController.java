package sc.server.distribution.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sc.server.distribution.services.CurrencyService;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/all")
    public ResponseEntity<?> allCurrencies(){
        return currencyService.getAllCurrencies();
    }

    @GetMapping("/processed")
    public ResponseEntity<?> processedCurrencies(){
        return currencyService.getProcessedCurrencies();
    }

}
