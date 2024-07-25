package sc.server.distribution.repositories;

import org.springframework.stereotype.Repository;
import sc.server.distribution.entities.Currency;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

//        currencies.add((new Currency("A")));
//        currencies.add((new Currency("B")));
//        currencies.add((new Currency("C")));
//        currencies.add((new Currency("D")));
//        currencies.add((new Currency("E")));
//        currencies.add((new Currency("F")));
//        currencies.add((new Currency("G")));
//        currencies.add((new Currency("H")));
//        currencies.add((new Currency("I")));
//        currencies.add((new Currency("J")));
//        currencies.add((new Currency("K")));
//        currencies.add((new Currency("L")));
//        currencies.add((new Currency("M")));
//        currencies.add((new Currency("N")));
//        currencies.add((new Currency("O")));
//        currencies.add((new Currency("P")));
//        currencies.add((new Currency("Q")));
//        currencies.add((new Currency("R")));
//        currencies.add((new Currency("S")));
//        currencies.add((new Currency("T")));
    }
    public List<Currency> findAll(){
        return currencies;
    }

    public Optional<Currency> findByName(String name){
        return currencies.stream().filter(currency -> currency.getName().equals(name)).findFirst();
    }

}
