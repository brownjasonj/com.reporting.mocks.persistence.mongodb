package com.reporting.mocks.persistence.mongodb;

import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.persistence.mongodb.model.TradePopulationEntry;
import com.reporting.mocks.persistence.mongodb.model.TradePopulationMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TradePopulationEntryRepository extends MongoRepository<TradePopulationEntry, TradePopulationId> {
    List<TradePopulationEntry> findByTradePopulationId(TradePopulationId id);
}
