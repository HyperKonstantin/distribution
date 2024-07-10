package sc.server.distribution.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sc.server.distribution.entities.Currency;

@Repository
public interface CurrencyRepository extends MongoRepository<Currency, Long> {
}
