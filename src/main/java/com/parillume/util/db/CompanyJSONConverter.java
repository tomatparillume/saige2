/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.db;

import com.parillume.model.CompanyModel;
import com.parillume.model.external.Company;
import com.parillume.model.external.User;
import com.parillume.util.CryptionUtil;
import com.parillume.util.JSONUtil;
import javax.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Component
public class CompanyJSONConverter implements AttributeConverter<String, String> {
    
    @Override
    public String convertToDatabaseColumn(String companyModelJSON) {
        return convert(companyModelJSON, true);
    }

    @Override
    public String convertToEntityAttribute(String companyModelJSON) {
        return convert(companyModelJSON, false);
    }
    
    public static String convert(String companyModelJSON, boolean toDB) {
        try {
            CompanyModel companyModel = JSONUtil.fromJSON(companyModelJSON, CompanyModel.class);
            Company company = companyModel.getCompany();
            company.setPassword( toDB ?
                                 CryptionUtil.encryptGlobal(company.getPassword()) :
                                 CryptionUtil.decryptGlobal(company.getPassword()) );
            
            for(User user: companyModel.getUsers()) {
                user.setPassword( toDB ?
                                  CryptionUtil.encryptGlobal(user.getPassword()) :
                                  CryptionUtil.decryptGlobal(user.getPassword()) );
            }
            
            return JSONUtil.toJSON(companyModel);
            
        } catch(Exception exc) {
            return companyModelJSON;
        }        
    }
}
