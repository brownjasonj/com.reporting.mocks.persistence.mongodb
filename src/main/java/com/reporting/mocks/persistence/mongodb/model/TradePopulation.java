package com.reporting.mocks.persistence.mongodb.model;

import com.reporting.mocks.interfaces.persistence.ITradePopulation;
import com.reporting.mocks.interfaces.persistence.ITradePopulationLive;
import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.trade.TradeType;
import com.reporting.mocks.persistence.mongodb.TradeRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TradePopulation implements ITradePopulation, ITradePopulationLive {
    protected TradePopulationMetaData tradePopulationMetaData;
    protected TradeRepository tradeRepository;
    protected HashSet<Tcn> listOfTcns;
    protected ConcurrentHashMap<TradeType, List<Tcn>> tradeTypeTrades;


    public TradePopulation() {super();}

    public TradePopulation(TradeRepository tradeRepository,
                           TradePopulationMetaData tradePopulationMetaData,
                           HashSet<Tcn> listOfTcns,
                           ConcurrentHashMap<TradeType, List<Tcn>> tradeTypeTrades) {
        this.tradeRepository = tradeRepository;
        this.tradePopulationMetaData = tradePopulationMetaData;
        this.listOfTcns = listOfTcns;
        this.tradeTypeTrades = tradeTypeTrades;
    }

    public TradePopulation(TradePopulation tradePopulation, DataMarkerType dataMarkerType) {
        this.tradeRepository = tradePopulation.tradeRepository;
        this.tradePopulationMetaData = new TradePopulationMetaData(tradePopulation.tradePopulationMetaData, dataMarkerType);
        this.listOfTcns = new HashSet<>(tradePopulation.listOfTcns);
        this.tradeTypeTrades = new ConcurrentHashMap<>();
        for(TradeType tradeType : tradePopulation.tradeTypeTrades.keySet()) {
            this.tradeTypeTrades.put(tradeType, new ArrayList<>(tradePopulation.tradeTypeTrades.get(tradeType)));
        }
    }
    public TradePopulation(TradeRepository tradeRepository, PricingGroup pricingGroup, DataMarkerType dataMarkerType) {
        this.tradeRepository = tradeRepository;
        this.tradePopulationMetaData = new TradePopulationMetaData(pricingGroup.getName(), dataMarkerType);
        this.listOfTcns = new HashSet<>();
        this.tradeTypeTrades = new ConcurrentHashMap<>();
    }

    public TradePopulationMetaData getTradePopulationMetaData() {
        return tradePopulationMetaData;
    }

    @Override
    public String getPricingGroupName() {
        return tradePopulationMetaData.getPricingGroupName();
    }

    public synchronized List<Tcn> getTcnsByTradeType(TradeType tradeType) {
        if (this.tradeTypeTrades.containsKey(tradeType)) {
            return this.tradeTypeTrades.get(tradeType);
        }
        else {
            return null;
        }
    }

    /*
            ITradePopulation interface implementation
         */
    @Override
    public TradePopulationId getId() {
        return tradePopulationMetaData.getTradePopulationId();
    }

    @Override
    public DataMarkerType getType() {
        return tradePopulationMetaData.getDataMarkerType();
    }

    @Override
    public Date getAsOf() {
        return tradePopulationMetaData.getAsOfDate();
    }

    @Override
    public synchronized Collection<Trade> getTrades() {
        ArrayList<Trade> trades = new ArrayList<>();
        for(Tcn tcn : this.listOfTcns) {
            Trade trade = this.tradeRepository.getTradeByTcn(tcn);
            if (trade != null) {
                trades.add(trade);
            }
        }
        return trades;
    }

    @Override
    public synchronized List<Trade> getByTradeType(TradeType tradeType) {
        ArrayList<Trade> trades = new ArrayList<>();
        if (this.tradeTypeTrades.containsKey(tradeType)) {
            List<Tcn> tradeTcns = this.tradeTypeTrades.get(tradeType);
            if (!tradeTcns.isEmpty()) {
                for(Tcn tcn : tradeTcns) {
                    Trade trade = this.tradeRepository.getTradeByTcn(tcn);
                    if (trade != null) {
                        trades.add(trade);
                    }
                }
            }
        }
        return trades;
    }

    @Override
    public synchronized List<TradeType> getTradeTypes() {
        return new ArrayList<>(this.tradeTypeTrades.keySet());
    }

    @Override
    public synchronized int getTradeCount() {
        return this.listOfTcns.size();
    }

    @Override
    public synchronized Trade getTrade(Tcn tcn) {
        if (this.listOfTcns.contains(tcn)) {
            return this.tradeRepository.getTradeByTcn(tcn);
        }
        else
            return null;
    }

    /*
        ITradePopulationMutable interface implementation
     */
    @Override
    public synchronized Trade add(Trade trade) {
        this.tradeRepository.save(trade);
        if (!this.listOfTcns.contains(trade.getTcn())) {
            List<Tcn> tcnsPerType;
            if (!this.tradeTypeTrades.containsKey(trade.getTradeType())) {
                tcnsPerType = new ArrayList<>();
                this.tradeTypeTrades.put(trade.getTradeType(), tcnsPerType);
            }
            else {
                tcnsPerType = this.tradeTypeTrades.get(trade.getTradeType());
            }
            tcnsPerType.add(trade.getTcn());
            this.listOfTcns.add(trade.getTcn());
        }
        return trade;
    }

    @Override
    public synchronized Trade oneAtRandom() {
        if (this.listOfTcns.isEmpty()) {
            return null;
        }
        else {
            Optional<Tcn> optionalTrade = this.listOfTcns.stream()
                    .skip((int) (this.listOfTcns.size() * Math.random()))
                    .findFirst();
            return this.getTrade(optionalTrade.get());
        }
    }

    @Override
    public synchronized Trade delete(Tcn tcn) {
        if (this.listOfTcns.contains(tcn)) {
            Trade trade = this.getTrade(tcn);
            if (this.tradeTypeTrades.containsKey(trade.getTradeType())) {
                this.tradeTypeTrades.get(trade.getTradeType()).remove(tcn);
            }
            this.listOfTcns.remove(tcn);
            return trade;
        }
        else {
            return null;
        }
    }
}

