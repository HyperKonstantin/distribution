package sc.server.distribution.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Currency {

    @Id
    private ObjectId id;
    private String name;
}
