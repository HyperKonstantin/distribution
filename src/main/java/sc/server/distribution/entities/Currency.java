package sc.server.distribution.entities;

import lombok.Data;

@Data
public class Currency {

    private String name;

    public Currency(String name){
        this.name= name;
    }
}
