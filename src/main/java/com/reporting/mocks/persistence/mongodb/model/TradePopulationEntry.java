package com.reporting.mocks.persistence.mongodb.model;

import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeType;
import org.springframework.data.annotation.Id;

public class TradePopulationEntry {
    protected TradePopulationId tradePopulationId;
    protected TradeType tradeType;
    protected Trade trade;

    public TradePopulationEntry(TradePopulationId tradePopulationId, Trade trade) {
        this.tradePopulationId = tradePopulationId;
        this.tradeType = trade.getTradeType();
        this.trade = trade;
    }

    public TradePopulationId getTradePopulationId() {
        return tradePopulationId;
    }

    public Tcn getTradeTcn() {
        return this.trade.getTcn();
    }

    public TradeType getTradeType() {
        return this.trade.getTradeType();
    }

    public Trade getTrade() { return this.trade; }
}
