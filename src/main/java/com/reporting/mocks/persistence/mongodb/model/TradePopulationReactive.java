package com.reporting.mocks.persistence.mongodb.model;

import com.reporting.mocks.interfaces.persistence.ITradePopulationReactive;
import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeType;
import com.reporting.mocks.persistence.mongodb.TradePopulationEntryReactiveRepository;
import com.reporting.mocks.persistence.mongodb.TradeRepository;
import reactor.core.publisher.Flux;

import java.util.Date;
import java.util.List;

public class TradePopulationReactive implements ITradePopulationReactive {
    protected TradePopulationMetaDataReactive tradePopulationMetaData;
    protected TradeRepository tradeRepository;
    protected TradePopulationEntryReactiveRepository tradePopulationEntryReactiveRepository;


    public TradePopulationReactive() {super();}

    public TradePopulationReactive(TradeRepository tradeRepository,
                                   TradePopulationEntryReactiveRepository tradePopulationEntryReactiveRepository,
                                   TradePopulationMetaDataReactive tradePopulationMetaData) {
        this.tradeRepository = tradeRepository;
        this.tradePopulationEntryReactiveRepository = tradePopulationEntryReactiveRepository;
        this.tradePopulationMetaData = tradePopulationMetaData;
    }

    public TradePopulationReactive(TradeRepository tradeRepository,
                                   TradePopulationEntryReactiveRepository tradePopulationEntryReactiveRepository,
                                   TradePopulation tradePopulation,
                                   DataMarkerType dataMarkerType) {
        this.tradeRepository = tradeRepository;
        this.tradePopulationEntryReactiveRepository = tradePopulationEntryReactiveRepository;
        this.tradePopulationMetaData = new TradePopulationMetaDataReactive(tradePopulation.tradePopulationMetaData, dataMarkerType);
        int tradeCount = 0;
        for(TradeType tradeType : tradePopulation.getTradeTypes()) {
            int tradeCountTradeType = tradePopulation.getTcnsByTradeType(tradeType).size();
            tradePopulationMetaData.setTradeCountByTradeType(tradeType, tradeCountTradeType);
            tradeCount += tradeCountTradeType;
        }
        this.tradePopulationMetaData.setTradeCount(tradeCount);
    }

    public TradePopulationMetaDataReactive getTradePopulationMetaDataReactive() {
        return this.tradePopulationMetaData;
    }

    @Override
    public TradePopulationId getId() {
        return this.tradePopulationMetaData.getTradePopulationId();
    }

    @Override
    public String getPricingGroupName() {
        return this.tradePopulationMetaData.getPricingGroupName();
    }

    @Override
    public DataMarkerType getType() {
        return this.tradePopulationMetaData.getDataMarkerType();
    }

    @Override
    public Date getAsOf() {
        return this.tradePopulationMetaData.getAsOfDate();
    }

    @Override
    public int getTradeCount() {
        return this.tradePopulationMetaData.getTradeCount();
    }

    @Override
    public int getTradeCountByTradeType(TradeType tradeType) {
        return this.tradePopulationMetaData.getTradeCountForTradeType(tradeType);
    }

    @Override
    public Flux<Trade> getTrades() {
        return this.tradePopulationEntryReactiveRepository.findByTradePopulationId(this.getId())
                        .map(tpe -> tpe.getTrade());
    }

    @Override
    public Flux<Trade> getTradesByType(TradeType tradeType) {
        if (this.tradePopulationMetaData.getTradeCountForTradeType(tradeType) > 0) {
            return this.tradePopulationEntryReactiveRepository.findByTradePopulationIdAndTradeType(this.getId(), tradeType)
                    .map(tpe -> tpe.getTrade());
        }
        else
            return null;
    }

    @Override
    public List<TradeType> getTradeTypes() {
        return this.tradePopulationMetaData.getTradeTypes();
    }

    @Override
    public Trade getTrade(Tcn tcn) { return this.tradeRepository.getTradeByTcn(tcn); }
}
