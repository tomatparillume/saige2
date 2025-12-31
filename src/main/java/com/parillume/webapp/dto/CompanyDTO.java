/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.model.CompanyModel;
import com.parillume.util.Constants;
import com.parillume.util.webapp.WebAppDisplayUtil;
import lombok.Data;


/**
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class CompanyDTO extends AbstractWebAppDTO {
    private CompanyModel companyModel;
    
    public CompanyDTO() {}
    
    public CompanyDTO(CompanyModel companyModel) 
    throws Exception {
        setId(companyModel.getCompany().getId());
        setLabel(companyModel.getCompany().getName());
        
        WebAppDisplayUtil.sortUsersByName(companyModel.getUsers());
        
        companyModel.getCompany().setPassword(Constants.PASSWORD_MASK);
        setCompanyModel(companyModel);
    }
}
