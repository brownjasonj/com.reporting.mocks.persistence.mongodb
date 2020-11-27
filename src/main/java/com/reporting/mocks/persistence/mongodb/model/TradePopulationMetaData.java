package com.reporting.mocks.persistence.mongodb.model;

import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.id.TradePopulationId;

import java.util.*;

public class TradePopulationMetaData {
    protected TradePopulationId tradePopulationId;
    protected String pricingGroupName;
    protected Date asOfDate;
    protected DataMarkerType dataMarkerType;

    public TradePopulationMetaData() {}

    public TradePopulationMetaData(TradePopulationId tradePopulationId, String pricingGroupName, DataMarkerType dataMarkerType) {
        this.tradePopulationId = tradePopulationId;
        this.pricingGroupName = pricingGroupName;
        this.dataMarkerType = dataMarkerType;
        this.asOfDate = new Date();
    }

    public TradePopulationMetaData(String pricingGroupName, DataMarkerType dataMarkerType) {
        this(new TradePopulationId(pricingGroupName, UUID.randomUUID()), pricingGroupName, dataMarkerType);
    }

    protected TradePopulationMetaData(TradePopulationMetaData tradePopulation, DataMarkerType dataMarkerType) {
        this.tradePopulationId = new TradePopulationId(tradePopulation.pricingGroupName, UUID.randomUUID());
        this.pricingGroupName = tradePopulation.pricingGroupName;
        this.dataMarkerType = dataMarkerType;
        this.asOfDate = new Date();
    }

    public TradePopulationId getId() {
        return tradePopulationId;
    }

    public TradePopulationId getTradePopulationId() {
        return tradePopulationId;
    }

    public String getPricingGroupName() {
        return pricingGroupName;
    }

    public Date getAsOfDate() {
        return asOfDate;
    }

    public DataMarkerType getDataMarkerType() {
        return dataMarkerType;
    }
}
