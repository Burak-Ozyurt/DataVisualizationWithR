package com.sau.pro1;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDataRepository
    extends MongoRepository<MongoData,String> {

    MongoData findByIdEquals(int id);
}
