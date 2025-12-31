/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.service;

import org.springframework.stereotype.Service;
import com.parillume.model.CompanyModel;
import com.parillume.model.external.User;
import java.util.Arrays;
import java.util.List;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
@Service
public class ModelValidatorService extends AbstractService {
    
    public void validateCompany(String corpusVersion, CompanyModel companyModel)
    throws Exception {
        ModelValidator validator = new ModelValidator(
                                        corpusService.get(corpusVersion),
                                        companyService.getCompanies() );
        validator.validateCompany(companyModel);        
        if(validator.hasErrors())
            throw new Exception(validator.getErrors().toString());       
    }
    
    public void validateUser(String corpusVersion, String companyId, User user)
    throws Exception {
        validateUsers(corpusVersion, companyId, Arrays.asList(user));
    }
    public void validateUsers(String corpusVersion, String companyId, List<User> users) 
    throws Exception {
        ModelValidator validator = new ModelValidator(
                                        corpusService.get(corpusVersion),
                                        companyService.getCompanies() );
        validator.validateUsers(companyService.get(companyId), users);
        if(validator.hasErrors())
            throw new Exception(validator.getErrors().toString()); 
    }
}
