package com.reporting.mocks.persistence.mongodb.model;

import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.TradeType;
import org.springframework.data.annotation.Id;

public class TradePopulationEntry {
    protected TradePopulationId tradePopulationId;
    protected Tcn tradeTcn;
    protected TradeType tradeType;

    public TradePopulationEntry(TradePopulationId tradePopulationId, Tcn tradeTcn, TradeType tradeType) {
        this.tradePopulationId = tradePopulationId;
        this.tradeTcn = tradeTcn;
        this.tradeType = tradeType;
    }

    public TradePopulationId getTradePopulationId() {
        return tradePopulationId;
    }

    public Tcn getTradeTcn() {
        return tradeTcn;
    }

    public TradeType getTradeType() {
        return tradeType;
    }
}
