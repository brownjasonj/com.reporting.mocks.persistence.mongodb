package com.reporting.mocks.persistence.mongodb;

import com.reporting.mocks.interfaces.persistence.IPersistenceAdmin;

import org.springframework.stereotype.Component;

@Component
public class MongoDBPersistenceAdmin implements IPersistenceAdmin {

    @Override
    public void clearDataSets() {

    }

    @Override
    public void initDataSets() {

    }
    
}