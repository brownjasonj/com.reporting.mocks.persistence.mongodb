package com.reporting.mocks.persistence.mongodb;

import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.persistence.mongodb.model.TradePopulationMetaDataReactive;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TradePopulationMetaDataReactiveRepository extends MongoRepository<TradePopulationMetaDataReactive, TradePopulationId> {
    TradePopulationMetaDataReactive getTradePopulationMetaDataReactiveByTradePopulationId(TradePopulationId id);
}
