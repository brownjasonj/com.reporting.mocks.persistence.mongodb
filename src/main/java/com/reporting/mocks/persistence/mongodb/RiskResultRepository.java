package com.reporting.mocks.persistence.mongodb;

import com.reporting.mocks.model.RiskResult;
import com.reporting.mocks.model.id.RiskRunId;
import com.reporting.mocks.model.id.TradePopulationId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RiskResultRepository extends MongoRepository<RiskResult,RiskRunId> {
    List<RiskResult> getAllByRiskRunId(RiskRunId riskRunId);
    List<RiskResult> getAllByTradePopulationId(TradePopulationId tradePopulationId);
}
