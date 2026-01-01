/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.controller;

import com.parillume.service.ConversionService;
import com.parillume.util.FileUtil.FileType;
import com.parillume.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tmargolis
 * @author tom@parillume.com
 */
@RestController
public class ConversionController extends AbstractController {
    
    @Autowired
    private ConversionService conversionService;

    public static void main(String[] args) throws Exception {  
        try {
            if(args == null || args.length != 2)
                throw new Exception("A [from] argument and a [to] argument are required; for example, 'PDF' and 'PNG'");
            
            String from = args[0];
            String to = args[1];
            
            FileType fromType = FileType.valueOf(from.toUpperCase());
            FileType toType = FileType.valueOf(to.toUpperCase());
            
            int conversionCount = new ConversionService().convert(fromType, toType, FileUtil.TMP_DIR);
            FileUtil.deleteErrorFile();
            System.out.println("*** Converted " + conversionCount + " files ***");
            
        } catch(Exception exc) {
            System.out.println("************* ERROR *************\n" + exc.getMessage());
            try {
                FileUtil.writeToErrorFile(exc.getMessage(), false);
            } catch(Exception ignored) {}
        }
    } 
    
    /**
     * Convert files of the specified 'from' type in the /tmp directory to
     * files of the specified 'to' type. The 'from' files are maintained.
     * 
     * @param fromType
     * @param toType
     * @return 
     */
    @GetMapping("/convertfiles")
    public ResponseEntity<String> convertFiles(@RequestParam(value = "from") FileType fromType,
                                               @RequestParam(value = "to") FileType toType) {
        try {
            int conversionCount = conversionService.convert(fromType, toType, FileUtil.TMP_DIR);

            return createResponse(conversionCount + " files converted from " + 
                                  fromType.name() + " to " + toType.name());
        } catch(Exception exc) {
            return createResponse("Failed to convert files", exc);
        }
    }
}
