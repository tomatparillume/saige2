/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.security;

import com.parillume.model.CompanyModel;
import com.parillume.model.external.Company;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class SessionTest {
    @Test
    public void testConstructors() throws Exception {
        constructors(true);
        constructors(false);
    }
    private void constructors(boolean userCredentials) throws Exception {
        Session session = new Session("a","b","c", userCredentials);
        String sessionId = session.toSessionID();
        Session session2 = new Session(sessionId);
        
        assertEquals(session, session2);
        assertEquals("a", session.getUsername());
        assertEquals("b", session.getPassword());
        assertEquals("c", session.getCompanyId());
        assertEquals(userCredentials, session.isUserCredentials());        
    }
    
    @Test
    public void testValidate() throws Exception {
        Company company = new Company();
        company.setUsername("a");
        company.setPassword("b");
        company.setId("c");
        
        CompanyModel companyModel = new CompanyModel();
        companyModel.setCompany(company);
        
        Session session = new Session("a","b","c", false);
        
        session.validate(companyModel, System.currentTimeMillis());
        
        try {
            session.validate(companyModel, 1000);
            fail("Expired timestamp erroneously considered valid");
        } catch(Exception exc) {}
        
        try {
            company.setUsername("aa");
            session.validate(companyModel, System.currentTimeMillis());
            fail("Invalid username erroneously considered valid");
        } catch(Exception exc) {}
        
        try {
            company.setUsername("a");
            company.setPassword("bb");
            session.validate(companyModel, System.currentTimeMillis());
            fail("Invalid password erroneously considered valid");
        } catch(Exception exc) {}
        
        try {
            company.setUsername("a");
            company.setPassword("b");
            company.setId("cc");
            session.validate(companyModel, System.currentTimeMillis());
            fail("Invalid company ID erroneously considered valid");
        } catch(Exception exc) {}
    }
}
