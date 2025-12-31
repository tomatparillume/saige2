/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.print.bargraph;

import com.parillume.print.input.BarGraphDataImporter;
import com.parillume.util.FileUtil;
import java.io.File;
import java.util.List;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**     
 * Generates bar graph images to insert into a survey PowerPoint.
 * See src/main/resources/template_survey_bargraph.xlsx
 * 
 * @author tmargolis
 * @author tom@parillume.com
 */
@RestController
public class BarGraphController {
    
    public static void main(String[] args) throws Exception {  
        try {
            int count = create();
            FileUtil.deleteErrorFile();
            System.out.println("*** Wrote " + count + " bar graphs ***");
            
        } catch(Exception exc) {
            System.out.println("************* ERROR *************\n" + exc.getMessage());
            try {
                FileUtil.writeToErrorFile(exc.getMessage(), false);
            } catch(Exception ignored) {}
        }
    }        
    
    @GetMapping(path = "/createbargraphs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBarGraphs() {
        try {     
            int count = create();
            
            JSONObject json = new JSONObject();
            try {
                json.put("message", count + " bar graphs created");
            } catch(Exception exc) {
                throw new Exception("Failed to build response: " + exc.getMessage());
            }            
            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
            
        } catch(Exception exc) {            
            return new ResponseEntity<>("{\"error\":\""+exc.getMessage()+"\"}", HttpStatus.BAD_REQUEST);  
        }          
    }
    
    private static int create() throws Exception {
        List<File> files = FileUtil.getTempBarGraphSheets();
        if(files.size() != 1)
            throw new Exception("Exactly one bar graph xlsx file must be provided");

        BarGraphDataImporter importer = new BarGraphDataImporter(files.get(0));        

        BarGraphWriter writer = new BarGraphWriter(importer);
        return writer.write();
    }
}
