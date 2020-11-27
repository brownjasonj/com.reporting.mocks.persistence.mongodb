package com.reporting.mocks.persistence.mongodb.model;

import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.trade.TradeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TradePopulationMetaDataReactive extends TradePopulationMetaData {
    protected HashMap<TradeType, Integer> tradeCountByTradeType;
    protected int tradeCount;

    public TradePopulationMetaDataReactive() {super();}

    public TradePopulationMetaDataReactive(TradePopulationMetaData tradePopulationMetaData, DataMarkerType dataMarkerType) {
        super(tradePopulationMetaData, dataMarkerType);
        this.tradeCountByTradeType = new HashMap<>();
        this.tradeCount = 0;
    }

    public void setTradeCountByTradeType(TradeType tradeType, int tradeCount) {
        this.tradeCountByTradeType.put(tradeType, tradeCount);
    }

    public int getTradeCountForTradeType(TradeType tradeType) {
        if (this.tradeCountByTradeType.containsKey(tradeType))
            return this.tradeCountByTradeType.get(tradeType);
        else
            return 0;
    }

    public List<TradeType> getTradeTypes() {
        return new ArrayList<>(this.tradeCountByTradeType.keySet());
    }

    public int getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
    }
}
