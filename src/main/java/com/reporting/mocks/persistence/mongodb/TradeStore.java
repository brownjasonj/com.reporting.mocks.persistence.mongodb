package com.reporting.mocks.persistence.mongodb;

import com.reporting.mocks.interfaces.persistence.ITradePopulation;
import com.reporting.mocks.interfaces.persistence.ITradePopulationLive;
import com.reporting.mocks.interfaces.persistence.ITradePopulationReactive;
import com.reporting.mocks.interfaces.persistence.ITradeStore;
import com.reporting.mocks.model.DataMarkerType;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.Tcn;
import com.reporting.mocks.model.trade.TradeType;
import com.reporting.mocks.persistence.mongodb.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TradeStore implements ITradeStore {
    protected PricingGroup pricingGroup;
    protected TradePopulation liveTradePopulation;
    protected TradePopulationMetaDataRepository tradePopulationMetaDataRepository;
    protected TradePopulationEntryRepository tradePopulationEntryRepository;
    protected TradePopulationEntryReactiveRepository tradePopulationEntryReactiveRepository;
    protected TradePopulationMetaDataReactiveRepository tradePopulationMetaDataReactiveRepository;
    protected TradeRepository tradeRepository;

    public TradeStore(PricingGroup pricingGroup,
                      TradePopulationMetaDataRepository tradePopulationRepository,
                      TradePopulationEntryRepository tradePopulationEntryRepository,
                      TradePopulationEntryReactiveRepository tradePopulationEntryReactiveRepository,
                      TradePopulationMetaDataReactiveRepository tradePopulationMetaDataReactiveRepository,
                      TradeRepository tradeRepository) {
        this.pricingGroup = pricingGroup;
        this.liveTradePopulation = new TradePopulation(tradeRepository, pricingGroup, DataMarkerType.LIVE);
        this.tradeRepository = tradeRepository;
        this.tradePopulationEntryRepository = tradePopulationEntryRepository;
        this.tradePopulationEntryReactiveRepository = tradePopulationEntryReactiveRepository;
        this.tradePopulationMetaDataReactiveRepository = tradePopulationMetaDataReactiveRepository;
        this.tradePopulationMetaDataRepository = tradePopulationRepository;
    }

    @Override
    public PricingGroup getPricingGroup() {
        return this.pricingGroup;
    }

    public void setPricingGroup(PricingGroup pricingGroup) {
        this.pricingGroup = pricingGroup;
    }


    private void saveTradePopulation(TradePopulation tradePopulation) {
        this.tradePopulationMetaDataRepository.save(tradePopulation.getTradePopulationMetaData());
        for(TradeType tradeType : tradePopulation.getTradeTypes()) {
            for(Tcn tradeTcn : tradePopulation.getTcnsByTradeType(tradeType)) {
                this.tradePopulationEntryRepository.save(new TradePopulationEntry(tradePopulation.getId(), this.tradeRepository.getTradeByTcn(tradeTcn)));
            }
        }
    }

    @Override
    public ITradePopulation createSnapShot(DataMarkerType type) {
        TradePopulation tpm = new TradePopulation(this.liveTradePopulation, type);
        saveTradePopulation(tpm);
        return tpm;
    }

    @Override
    public ITradePopulationLive getTradePopulationLive() {
        return liveTradePopulation;
    }

    @Override
    public ITradePopulation getTradePopulationById(TradePopulationId id) {
        TradePopulationMetaData tradePopulationStub = this.tradePopulationMetaDataRepository.getTradePopulationMetaDataByTradePopulationId(id);
        List<TradePopulationEntry> tradePopulationEntries = this.tradePopulationEntryRepository.findByTradePopulationId(id);
        HashSet<Tcn> listOfTcns = new HashSet<>();
        ConcurrentHashMap<TradeType, List<Tcn>> tradeTypeTrades = new ConcurrentHashMap<>();

        for(TradePopulationEntry tradePopulationEntry : tradePopulationEntries) {
            listOfTcns.add(tradePopulationEntry.getTradeTcn());
            List<Tcn> tradeTypeTcns;
            if (!tradeTypeTrades.containsKey(tradePopulationEntry.getTradeType())) {
                tradeTypeTcns = new ArrayList<>();
                tradeTypeTrades.put(tradePopulationEntry.getTradeType(), tradeTypeTcns);
            }
            else {
                tradeTypeTcns = tradeTypeTrades.get(tradePopulationEntry.getTradeType());
            }
            tradeTypeTcns.add(tradePopulationEntry.getTradeTcn());
        }
        return new TradePopulation(this.tradeRepository, tradePopulationStub, listOfTcns, tradeTypeTrades);
    }

    @Override
    public Collection<ITradePopulation> getAllTradePopulation() {
        ArrayList<ITradePopulation> tradePopulations = new ArrayList<>();
        for(TradePopulationMetaData tradePopulationStub : this.tradePopulationMetaDataRepository.findAll()) {
            tradePopulations.add(getTradePopulationById(tradePopulationStub.getId()));
        }
        return tradePopulations;
    }


    @Override
    public List<TradePopulationId> getTradePopulationsIds() {
        return this.tradePopulationMetaDataRepository.findAll().stream().map(tradePopulation -> tradePopulation.getId()).collect(Collectors.toList());
    }

    @Override
    public ITradePopulationReactive createReactiveSnapShot(DataMarkerType type) {
        TradePopulation tpm = new TradePopulation(this.liveTradePopulation, type);
        TradePopulationReactive tradePopulationReactive = new TradePopulationReactive(this.tradeRepository,
                this.tradePopulationEntryReactiveRepository,
                tpm,
                type);
        this.tradePopulationMetaDataReactiveRepository.save(tradePopulationReactive.getTradePopulationMetaDataReactive());

        for(TradeType tradeType : tpm.getTradeTypes()) {
            for(Tcn tradeTcn : tpm.getTcnsByTradeType(tradeType)) {
                this.tradePopulationEntryRepository.save(new TradePopulationEntry(tradePopulationReactive.getId(), this.tradeRepository.getTradeByTcn(tradeTcn)));
            }
        }
        return tradePopulationReactive;
    }

    @Override
    public ITradePopulationReactive getTradePopulationReactiveById(TradePopulationId id) {
        TradePopulationMetaDataReactive tradePopulationMetaDataReactive = this.tradePopulationMetaDataReactiveRepository.getTradePopulationMetaDataReactiveByTradePopulationId(id);
        return new TradePopulationReactive(
                this.tradeRepository,
                this.tradePopulationEntryReactiveRepository,
                tradePopulationMetaDataReactive);
    }
}
