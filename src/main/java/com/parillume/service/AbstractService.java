/*
 * Copyright(c) 2024, Parillume Inc., All rights reserved worldwide
 */
package com.parillume.service;

import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Service
public class AbstractService {
    
    @Autowired
    protected CorpusService corpusService;
    
    @Autowired
    protected CompanyService companyService; 
    
    public CompanyModel getCompany(String companyId) throws Exception {
        CompanyModel company = companyService.get(companyId);
        if(company == null)
            throw new Exception("Company " + companyId + " not found");
        return company;
    }

    protected CorpusModel getCorpus(String corpusVersion) throws Exception {
        CorpusModel corpus = corpusService.get(corpusVersion);
        if(corpus == null)
            throw new Exception("Corpus version " + corpusVersion + " not found"); 
        return corpus;
    }    
}
