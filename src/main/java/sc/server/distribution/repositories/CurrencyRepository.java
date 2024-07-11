package sc.server.distribution.repositories;

import org.springframework.stereotype.Repository;
import sc.server.distribution.entities.Currency;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CurrencyRepository {
    private final List<Currency> currencies = new ArrayList<>();

    {
        currencies.add((new Currency("BTC")));
        currencies.add((new Currency("ETH")));
        currencies.add((new Currency("USDT")));
        currencies.add((new Currency("BNB")));
        currencies.add((new Currency("SOL")));
        currencies.add((new Currency("USDC")));
        currencies.add((new Currency("XRP")));
        currencies.add((new Currency("TON")));
        currencies.add((new Currency("DOGE")));
        currencies.add((new Currency("ADA")));
    }
    public List<Currency> findAll(){
        return currencies;
    }

}
