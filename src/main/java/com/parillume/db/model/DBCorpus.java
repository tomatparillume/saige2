/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.db.model;

import com.parillume.model.CorpusModel;
import com.parillume.util.JSONUtil;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
@Entity
@Table(name = "corpus")
public class DBCorpus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "version")
    private String version;
    
    @Column(name = "corpusJSON", columnDefinition="TEXT")
    private String corpusJSON;
    
    public DBCorpus() {}
    
    public DBCorpus(CorpusModel corpusModel) throws Exception {
        setVersion(corpusModel.getVersion());        
        setCorpusJSON(JSONUtil.toJSON(corpusModel));        
    }
}
