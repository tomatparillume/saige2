/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.external.chat.model;

import com.parillume.external.model.QueryBuilder;
import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class CustomChatGPTQueryBuilder extends QueryBuilder {

    public CustomChatGPTQueryBuilder(ChatMetadata chatMetadata, CompanyModel company, CorpusModel corpus) {
        super(chatMetadata, company, corpus);
    }
    
    @Override
    public void generateQuery() throws Exception {
        throw new NotImplementedException("Custom ChatGPT queries are not yet implemented");
    }        
}
