package com.sau.pro1;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pro1")
@Getter
@Setter

public class MongoData {

    @Id
    private String mongoId;
    private  int id;
    private double value;

}
