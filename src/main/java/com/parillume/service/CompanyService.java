/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.service;

import com.parillume.db.model.DBCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.parillume.db.repository.CompanyRepository;
import com.parillume.model.CompanyModel;
import com.parillume.util.JSONUtil;
import com.parillume.util.StringUtil;
import com.parillume.util.db.CompanyJSONConverter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Service
public class CompanyService {
    @Autowired 
    private CompanyRepository companyRepository;
    
    /**
     * Returns {companyId : [company username, company name]}
     */
    public Map<String,Pair<String,String>> getCompanyReferences() {
        return companyRepository.findAll()
                                .stream()
                                .map(c -> fromJSON(c))
                                .collect(Collectors.toMap(c->c.getCompany().getId(), 
                                                          c->MutablePair.of(c.getCompany().getUsername(),c.getCompany().getName()))
                                        );
    }
    
    public List<CompanyModel> getCompanies() {
        return companyRepository.findAll()
                                .stream()
                                .map(c -> fromJSON(c))
                                .collect(Collectors.toList());
    }    
    
    public CompanyModel get(String username, String password) throws Exception {
        Optional<CompanyModel> optComp = companyRepository.findAll()
                                                          .stream()
                                                          .map(c -> fromJSON(c))
                                                          .filter(model -> model != null &&
                                                                           StringUtil.nullEquals(username, model.getCompany().getUsername()) &&
                                                                           StringUtil.nullEquals(password, model.getCompany().getPassword()) 
                                                          ).findFirst();
        return optComp.isPresent() ? 
               optComp.get() : 
               null;
    }
    
    public CompanyModel get(String companyId) throws Exception {
        DBCompany dbCompany = companyRepository.findByCompanyId(companyId);
        return dbCompany != null ?
               fromJSON(dbCompany) : // May be null
               null;
    }
    
    public void add(CompanyModel company) throws Exception {
        DBCompany dbCompany = new DBCompany(company);
        companyRepository.save(dbCompany);
    }
    
    public void update(CompanyModel company) throws Exception {
        DBCompany dbCompany = new DBCompany(company);
        String json = CompanyJSONConverter.convert(dbCompany.getCompanyJSON(), true);
        companyRepository.updateCompany(json, dbCompany.getCompanyId());
    }
    
    public void delete(String companyId) {
        companyRepository.deleteByCompanyId(companyId);
    }
    
    private static CompanyModel fromJSON(DBCompany dBCompany) {
        CompanyModel model = null;
        try {
            model = JSONUtil.fromJSON(dBCompany.getCompanyJSON(), CompanyModel.class);
        } catch(Exception exc) {
            //TODO logging
        }
        return model;
    }
}
