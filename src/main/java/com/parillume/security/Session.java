/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.security;

import com.parillume.error.ExpiredSessionException;
import com.parillume.model.CompanyModel;
import com.parillume.model.external.Company;
import com.parillume.util.CryptionUtil;
import com.parillume.util.StringUtil;
import java.util.Objects;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class Session {
    private static final String SESSION_ID_DELIMITER = "-__-";
    
    // public for testing:
    public static final long SESSION_DURATION_MS = 1000 * 60 * 30; // 30 minutes
    
    private boolean userCredentials = false;
    
    private String username; // Company username or User username
    /**
     * The Company password allows basic access
     * A User's password allows editing of that User
     */
    private String password; // Company password or User password
    private String companyId;

    public Session() {}
    
    public Session(String username, String password, String companyId,
                   boolean userCredentials) {
        this.username = username;
        this.password = password;
        this.companyId = companyId;
        this.userCredentials = userCredentials;
    }
    
    public Session(String sessionId) throws Exception {
        try {
            String[] parts = sessionId.split(SESSION_ID_DELIMITER);
            
            this.username = CryptionUtil.decryptGlobal(parts[0]);
            this.password = CryptionUtil.decryptGlobal(parts[1]);
            this.companyId = CryptionUtil.decryptGlobal(parts[2]);    
            this.userCredentials = Boolean.parseBoolean(parts[3]);
        } catch(Exception exc) {
            throw new Exception(sessionId + " is not a valid sessionId", exc);
        }
    }
    
    public static Session getParillumeSession() {        
        return new Session("info@parillume.com", "LSFAbundance", "plume-company", false);
    }
    
    public String toSessionID() {
        return CryptionUtil.encryptGlobal(username) + SESSION_ID_DELIMITER +
               CryptionUtil.encryptGlobal(password) + SESSION_ID_DELIMITER +
               CryptionUtil.encryptGlobal(companyId) + SESSION_ID_DELIMITER +
               userCredentials;
    }

    public void validate(CompanyModel companyModel, long sessionTimeStamp) throws Exception {
        if( StringUtil.isEmpty(username) || StringUtil.isEmpty(password) || StringUtil.isEmpty(companyId)) {
            throw new Exception("Username, password, and/or company ID are missing");
        }
        
        if( System.currentTimeMillis() - sessionTimeStamp > SESSION_DURATION_MS ) {
            throw new ExpiredSessionException();
        }

        Company company = companyModel.getCompany();
        if(company == null)
            throw new Exception("Company " + getCompanyId() + " not found");
        
        if( !StringUtil.nullEquals(companyId, company.getId()) )
            throw new Exception("Company ID is invalid");    
        
        verifyCredentials(companyModel);
    }
    
    public void verifyCredentials(CompanyModel companyModel) 
    throws Exception {
        boolean validCredentials = userCredentials ?
                                    // Does any user match this session?
                                    companyModel.getUsers()
                                                .stream()
                                                .anyMatch(u -> StringUtil.nullEquals(username, u.getEmailAddress()) &&
                                                               StringUtil.nullEquals(password, u.getPassword()))
                                    :
                                    // Does this company match this session?
                                    StringUtil.nullEquals(username, companyModel.getCompany().getUsername()) &&
                                    StringUtil.nullEquals(password, companyModel.getCompany().getPassword());        
        if(!validCredentials) {
            throw new Exception("Username and/or password are invalid");
        }        
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Session other = (Session) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        return Objects.equals(this.companyId, other.companyId);
    }
    
}
