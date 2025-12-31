/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.controller;

import com.parillume.model.CompanyModel;
import com.parillume.model.external.Team;
import com.parillume.model.external.User;
import com.parillume.print.PrintWriterIF;
import com.parillume.print.ProcessArg;
import com.parillume.print.WorksheetController;
import com.parillume.print.input.WorksheetDataImporter;
import com.parillume.security.Session;
import com.parillume.security.service.PermissionsService;
import com.parillume.security.service.SessionService;
import com.parillume.service.ImageService;
import com.parillume.util.FileUtil;
import com.parillume.util.StringUtil;
import com.parillume.util.print.PrintWriterUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@RestController
public class SheetsController extends AbstractController {
        
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private PermissionsService permissionsService;
    
    @Autowired
    private ImageService imageService;
    
    @GetMapping(path = "/commandline") 
    public ResponseEntity<String> commandLine(@RequestParam(value = "arg") List<String> args) {
        try {
            WorksheetController.main(args.toArray(new String[0]));
            return createResponse("Success!");            
        } catch(Exception exc) {  
            return createResponse("Failed to call command line", exc);
        }           
    }   
    
    @GetMapping(path = "/getmaxsheetcounts") 
    public ResponseEntity<String> getMaxSheetCounts() {
        Map<ProcessArg,Integer> sheetTypeToMaxCount = new HashMap<>();
        
        try {
            for(ProcessArg process: ProcessArg.values()) {
                PrintWriterIF writer = PrintWriterUtil.getWriter(process, null, "", null, null);
                sheetTypeToMaxCount.put(process, writer.getMaxUsers(process));
            }
            return createObjectResponse(sheetTypeToMaxCount);
            
        } catch(Exception exc) {  
            return createResponse("Failed to get maximum sheet count", exc);
        }      
    }
    
    /**
     * Create one-sheets from users in the referenced company.
     * If userIds are not submitted, all users in the company will be targeted.
     */
    @GetMapping(path = "/createonesheets") 
    public ResponseEntity<String> createOneSheets(@RequestParam(value = "companyid") String companyId,
                                                  @RequestParam(value = "userid", required=false) List<String> userIds,
                                                  @RequestParam(value = "sessionid") String sessionId) {
        String msg = "one-sheets for company " + companyId + 
                     (userIds != null && !userIds.isEmpty() ? " and users " + userIds : "");
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "createonesheets"); 
            
            writeUserSheets(companyId, userIds, ProcessArg.onesheets);
            return createResponse("Created " + msg);
        } catch(Exception exc) {  
            return createResponse("Failed to create " + msg, exc);
        }      
    }
    
    /**
     * Create multi-sheets from users in the referenced company.
     * If userIds are not submitted, all users in the company will be targeted.
     */
    @GetMapping(path = "/createmultisheets") 
    public ResponseEntity<String> createMultiSheets(@RequestParam(value = "companyid") String companyId,
                                                    @RequestParam(value = "userid", required=false) List<String> userIds,
                                                    @RequestParam(value = "sessionid") String sessionId) {
        String msg = "multi-sheets for company " + companyId + 
                     (userIds != null && !userIds.isEmpty() ? " and users " + userIds : "");
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "createmultisheets"); 
            
            writeUserSheets(companyId, userIds, ProcessArg.multisheets);
            return createResponse("Created " + msg);
        } catch(Exception exc) {            
            return createResponse("Failed to create " + msg, exc);
        }      
    }
    
    @GetMapping(value = "/getresourcefilebytes")
    public ResponseEntity<String> getResourceFileBytes(@RequestParam(value = "resourceid") String resourceId,
                                                       @RequestParam(value = "sessionid") String sessionId) 
    throws Exception {
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "getresourcefilebytes"); 
            
            File resource = FileUtil.getResource(resourceId);
            byte[] fileBytes = FileUtils.readFileToByteArray(resource);
            
            Map<String, String> fileNameToEncodedStrings = new HashMap<>();
            fileNameToEncodedStrings.put(resource.getName(), Base64.getEncoder().encodeToString(fileBytes));
            
            return createObjectResponse(fileNameToEncodedStrings);
            
        } catch(Exception exc) {  
            return createResponse("Failed to upload resource " + resourceId, exc);
        }         
    }
    
    @GetMapping(value = "/getteamchartbytes")
    public ResponseEntity<String> getTeamChartBytes(@RequestParam(value = "companyid") String companyId,
                                                    @RequestParam(value = "teamname") String customTeamName,
                                                    @RequestParam(value = "userid") List<String> userIds,
                                                    @RequestParam(value = "sessionid") String sessionId) 
    throws Exception {
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "getteamchartbytes");
            
            if(StringUtil.isEmpty(customTeamName))
                customTeamName = "";
            
            if(userIds == null || userIds.size() < 2)
                throw new Exception("At least two userIds are required to generate a custom team chart");
            
            Map<String, byte[]> fileNameToContents = generateSheets( companyId, ProcessArg.teamcharts, userIds, null, 
                                                                     customTeamName, true );            
            // We generate a single team chart:
            Map<String,String> fileNameToEncodedStrings = 
                                fileNameToContents.entrySet()
                                                  .stream()
                                                  .collect(Collectors.toMap(e->e.getKey(), e->Base64.getEncoder().encodeToString(e.getValue())));
            
            return createObjectResponse(fileNameToEncodedStrings);
        } catch(Exception exc) {      
            return createResponse("Failed to get team chart bytes for company " + companyId + " and " +
                                  (userIds != null && !userIds.isEmpty() ? " and users " + userIds : ""), exc);
        } 
    }    
    
    @GetMapping(value = "/getmultisheetbytes")
    public ResponseEntity<String> getMultiSheetBytes(@RequestParam(value = "companyid") String companyId,
                                                     @RequestParam(value = "userid") List<String> userIds,
                                                     @RequestParam(value = "sessionid") String sessionId) 
    throws Exception {
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "getmultisheetbytes");
            
            if(userIds == null || userIds.isEmpty())
                throw new Exception("User ids must be specified to generate one-sheet charts");
            
            Map<String,byte[]> fileNameToContents = generateSheets( companyId, ProcessArg.multisheets, userIds, null, 
                                                                    null, true );            
            // filesContent.get(0): We generate a single multisheet:
            Map<String,String> fileNameToEncodedStrings = 
                                fileNameToContents.entrySet()
                                                  .stream()
                                                  .collect(Collectors.toMap(e->e.getKey(), e->Base64.getEncoder().encodeToString(e.getValue())));
            return createObjectResponse(fileNameToEncodedStrings);
        } catch(Exception exc) {      
            return createResponse("Failed to get team chart bytes for company " + companyId, exc);
        } 
    }    
    
    private void writeUserSheets(String companyId, List<String> userIds, ProcessArg process) 
    throws Exception {
        generateSheets(companyId, process, userIds, "", null, false);
    }
    
    /**
     * getByteArray=true returns map of {fileName, byte[]}
     * getByteArray=false writes sheet to disk and returns null
     */
    private Map<String, byte[]> generateSheets(String companyId, ProcessArg process,
                                        // For ProcessArg.onesheets and .multisheets
                                        List<String> userIds, 
                                        // For ProcessArg.teamcharts:
                                        String teamId, 
                                        String customTeamName, // For custom team charts
                                        boolean getByteArray) 
    throws Exception {
        if(userIds == null)
            userIds = new ArrayList<>();
        
        CompanyModel company = companyService.get(companyId);
        if(company == null)
            throw new Exception("Company " + companyId + " not found");

        List<User> users = company.getUsers();
        List<User> sheetUsers = new ArrayList(users);
        
        String teamName = null; // Used to create teamcharts
        final Set userIdsFinal = new HashSet(userIds);
        if(ProcessArg.teamcharts == process) {
            
            if( !StringUtil.isEmpty(teamId)) {
                Optional<Team> teamOpt = company.getTeams().stream()
                                                .filter(t -> StringUtil.nullEquals(teamId, t.getId()))
                                                .findFirst();
                if(teamOpt.isEmpty())
                    throw new Exception("Company " + companyId + " does not contain team " + teamId);  

                sheetUsers.removeIf(u -> !u.getTeamIdToRoleId().keySet().contains(teamId));
                teamName = teamOpt.get().getName();   

            // Generate a custom team chart
            } else if(!userIds.isEmpty()) {
                sheetUsers.removeIf(u -> !userIdsFinal.contains(u.getId()));
                teamName = customTeamName;          
                
            // teamId is empty: All users in the company will be included
            } else {
                teamName = company.getCompany().getName();
            }               
        
        } else if(!userIds.isEmpty()) {
            sheetUsers.removeIf(u -> !userIdsFinal.contains(u.getId()));
        }
        
        if(sheetUsers.isEmpty()) {
            throw new Exception("Company " + companyId + " does not contain users" +
                                (!userIds.isEmpty() ? " " + userIds : "")
            );
        }

        WorksheetDataImporter importer = new WorksheetDataImporter( sheetUsers.toArray(new User[0]),
                                                                    users.toArray(new User[0]) );
        
        // If null, clientLogo will be read from the local tmp/ directory:
        PrintWriterIF writer = PrintWriterUtil.getWriter(process, importer, teamName,
                                                         imageService, companyId);
        if(getByteArray) {
            // If imageType != null, write the client logo
            return writer.getBytes(); 
        } else {
            writer.write(); // Always attempt to write the client logo
            return null;
        }
    }
}
