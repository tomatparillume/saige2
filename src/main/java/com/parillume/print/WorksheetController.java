/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.print;

import com.parillume.print.input.WorksheetDataImporter;
import com.parillume.util.FileUtil;
import com.parillume.util.print.PrintWriterUtil;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts *.xlsx files (based on src/main/resources/WorksheetTemplate.xlsx) to
 * one-sheet PDFs.
 * 
 * See README.md
 *          
 * @author tmargolis
 * @author tom@parillume.com
 */
public class WorksheetController {
    
    public static void main(String[] args) throws Exception {  
        Map<String,String> argsMap = getArgsMap(args);
        
        try {
            ProcessArg process = ProcessArg.getProcessArg(argsMap);
            Map<VariablesArg,String> varsMap = VariablesArg.getVariablesArgs(argsMap);
            
            List<File> files = FileUtil.getTempWorksheets();
            
            WorksheetDataImporter importer = new WorksheetDataImporter(files);
            
            // Only used for team charts:
            String teamName = varsMap.containsKey(VariablesArg.teamname) ?
                              varsMap.get(VariablesArg.teamname) :
                              "";                
          
            PrintWriterUtil.getWriter(process, importer, teamName, null, null).write();

            FileUtil.deleteErrorFile();

            System.out.println("*** Wrote " + process.getLabel() + " for " + files.size() + " people ***");
            
        } catch(Exception exc) {
            System.out.println("************* ERROR *************\n" + exc.getMessage());
            try {
                FileUtil.writeToErrorFile(exc.getMessage(), false);
            } catch(Exception ignored) {}
        }
    }    
    
    private static Map<String,String> getArgsMap(String[] args) {        
        return Arrays.asList(args)
                     .stream()
                     .filter(a -> a.contains("="))
                     .collect(Collectors.toMap(a -> a.split("=")[0], 
                                               a -> a.split("=")[1]));        
    }
}
