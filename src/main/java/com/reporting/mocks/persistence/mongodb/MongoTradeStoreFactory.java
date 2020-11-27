package com.reporting.mocks.persistence.mongodb;


import com.reporting.mocks.interfaces.persistence.IPersistenceStoreFactory;
import com.reporting.mocks.interfaces.persistence.ITradeStore;
import com.reporting.mocks.model.PricingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope
public class MongoTradeStoreFactory implements IPersistenceStoreFactory<ITradeStore> {
    @Autowired
    protected TradePopulationMetaDataRepository tradePopulationRepository;
    @Autowired
    protected TradePopulationEntryRepository tradePopulationEntryRepository;
    @Autowired
    protected TradePopulationEntryReactiveRepository tradePopulationEntryReactiveRepository;
    @Autowired
    protected TradePopulationMetaDataReactiveRepository tradePopulationMetaDataReactiveRepository;
    @Autowired
    protected TradeRepository tradeRepository;

    protected ConcurrentHashMap<String, ITradeStore> tradeStores;

    @Autowired
    protected MongoTradeStoreFactory(TradePopulationMetaDataRepository tradePopulationRepository,
                                     TradePopulationEntryRepository tradePopulationEntryRepository,
                                     TradePopulationEntryReactiveRepository tradePopulationEntryReactiveRepository,
                                     TradePopulationMetaDataReactiveRepository tradePopulationMetaDataReactiveRepository,
                                     TradeRepository tradeRepository) {
        this.tradeStores = new ConcurrentHashMap<>();
        this.tradePopulationRepository = tradePopulationRepository;
        this.tradePopulationEntryRepository = tradePopulationEntryRepository;
        this.tradePopulationEntryReactiveRepository = tradePopulationEntryReactiveRepository;
        this.tradePopulationMetaDataReactiveRepository = tradePopulationMetaDataReactiveRepository;
        this.tradeRepository = tradeRepository;
    }

    @Override
    public ITradeStore create(PricingGroup pricingGroupName) {
        ITradeStore store = new TradeStore(pricingGroupName,
                this.tradePopulationRepository,
                this.tradePopulationEntryRepository,
                this.tradePopulationEntryReactiveRepository,
                this.tradePopulationMetaDataReactiveRepository,
                this.tradeRepository);
        this.tradeStores.put(store.getPricingGroup().getName(), store);
        return store;
    }

    @Override
    public ITradeStore get(PricingGroup pricingGroup) {
        if (this.tradeStores.containsKey(pricingGroup.getName()))
            return this.tradeStores.get(pricingGroup.getName());
        else
            return null;
    }

    @Override
    public ITradeStore delete(PricingGroup pricingGroup) {
        if (this.tradeStores.containsKey(pricingGroup.getName()))
            return this.tradeStores.remove(pricingGroup.getName());
        else
            return null;
    }
}
