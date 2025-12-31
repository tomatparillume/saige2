/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.service;

import com.parillume.db.model.DBCorpus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.parillume.db.repository.CorpusRepository;
import com.parillume.model.CorpusModel;
import com.parillume.util.JSONUtil;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Service
public class CorpusService {
    @Autowired 
    CorpusRepository repo;
        
    public CorpusModel get(String version) throws Exception {
        DBCorpus dbCorpus = repo.findByVersion(version);
        return dbCorpus != null ?
               JSONUtil.fromJSON(dbCorpus.getCorpusJSON(), CorpusModel.class) :
               null;
    }
    
    public void add(CorpusModel corpus) throws Exception {
        DBCorpus dbCorpus = new DBCorpus(corpus);
        repo.save(dbCorpus);
    }
    
    public void update(CorpusModel corpus) throws Exception {
        DBCorpus dbCorpus = new DBCorpus(corpus);
        repo.updateCorpus(dbCorpus.getCorpusJSON(), dbCorpus.getVersion());
    }
    
    public void delete(String version) {
        repo.deleteByCorpusVersion(version);
    }
}
