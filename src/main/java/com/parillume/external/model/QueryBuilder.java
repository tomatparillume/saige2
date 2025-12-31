/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.external.model;

import com.parillume.external.chat.model.ChatMetadata;
import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public abstract class QueryBuilder {
    private ChatMetadata chatMetadata;
    private CompanyModel company;
    private CorpusModel corpus;
    
    protected QueryBuilder(ChatMetadata chatMetadata, CompanyModel company, CorpusModel corpus) {
        setChatMetadata(chatMetadata);
        setCompany(company);
        setCorpus(corpus);
    }
    
    /**
     * Generates a query and sets it on our ChatMetadata
     */
    public abstract void generateQuery() throws Exception;
}
