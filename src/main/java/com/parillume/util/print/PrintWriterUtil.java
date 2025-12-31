/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.util.print;

import com.parillume.print.PrintWriterIF;
import com.parillume.print.ProcessArg;
import static com.parillume.print.ProcessArg.multisheets;
import static com.parillume.print.ProcessArg.onesheets;
import static com.parillume.print.ProcessArg.teamcharts;
import com.parillume.print.input.WorksheetDataImporter;
import com.parillume.print.onesheet.OneSheetWriter;
import com.parillume.print.teamcharts.TeamChartsWriter;
import com.parillume.service.ImageService;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class PrintWriterUtil {
    
    public static PrintWriterIF getWriter(ProcessArg process,
                                          WorksheetDataImporter importer,
                                          // Not used by all writers:
                                          String teamName,
                                          // For writing client logos to team charts:
                                          ImageService imageService, String companyId)
    throws Exception {
        PrintWriterIF writer = null;
        switch(process) {
            case onesheets:
                writer = new OneSheetWriter(importer, false);
                break;
            case multisheets:
                writer = new OneSheetWriter(importer, true);
                break;
            case teamcharts:           
                writer = new TeamChartsWriter(importer, teamName, imageService, companyId);
                break;
            default:
                throw new Exception("ProcessArg " + process.name() + " is not supported");
        }
        
        return writer;
    }
}
