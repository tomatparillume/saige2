/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class FileUtil {
    public static final File RESOURCES_DIR = new File(new File("."), "src/main/resources");
    public static final File FONTS_DIR = RESOURCES_DIR;
    public static final File IMAGES_DIR = new File(RESOURCES_DIR, "images");
    public static final File TEMPLATES_DIR = RESOURCES_DIR;
    
    // Used solely for localhost execution, where a /tmp directory is placed
    // within the parent directory of the localhost project:
    public static final File TMP_DIR = new File(new File("."), "tmp");
    public static final File ERROR_FILE = new File(TMP_DIR, "errors.txt");
    
    private static final Map<String, File> resourceIdToResourceFile = new HashMap<>();
    static {
        resourceIdToResourceFile.put("WORKSHEET_TEMPLATE", new File(RESOURCES_DIR, "WorksheetTemplate.xlsx"));
    }
    
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
    public static FilenameFilter worksheetFilter = 
                                    new FilenameFilter() {
                                        @Override
                                        public boolean accept(File file, String fileName) {
                                            return fileName.endsWith(".xlsx") &&
                                                   !fileName.contains(Constants.BARGRAPH_FILE_SUFFIX) &&
                                                   // This file is a temp version of an opened doc:
                                                   !fileName.contains("~"); 
                                        } 
                                    };
    
    public static FilenameFilter barGraphFilter = 
                                    new FilenameFilter() {
                                        @Override
                                        public boolean accept(File file, String fileName) {
                                            return fileName.endsWith(".xlsx") &&
                                                   fileName.contains(Constants.BARGRAPH_FILE_SUFFIX) &&
                                                   // This file is a temp version of an opened doc:
                                                   !fileName.contains("~"); 
                                        } 
                                    };    
            
    public static FileInputStream toStream(File f) {
        try {
            return new FileInputStream(f);
        } catch(Exception exc) {
            throw new RuntimeException(exc);
        }
    }
    
    public static File getResource(String resourceId) throws Exception {
        File resource = resourceIdToResourceFile.get(resourceId);
        if(resource == null)
            throw new FileNotFoundException("Resource " + resourceId + " not found");
        return resource;
    }
    
    public static List<File> getTempWorksheets() throws Exception {
        return getSheets(worksheetFilter, "worksheet");
    }
    
    public static List<File> getTempBarGraphSheets() throws Exception {
        return getSheets(barGraphFilter, "bar graph");
    }
    
    private static List<File> getSheets(FilenameFilter filter, String fileLabel) 
    throws Exception {
        File[] files = FileUtil.TMP_DIR.listFiles(filter);
        if(files == null)
            throw new Exception("No " + FileUtil.TMP_DIR + " directory found");
        else if(files.length == 0)
            throw new Exception("No " + fileLabel + " file(s) found in " + FileUtil.TMP_DIR);
        
        return Arrays.asList(files).stream().collect(Collectors.toList()); 
    }
    
    public static void writeToErrorFile(String content, boolean append) throws IOException {
        writeToFile(ERROR_FILE, dateFormat.format(new java.util.Date()) + " - " + content, append);
    }
    
    public static void deleteErrorFile() {
        ERROR_FILE.delete();
    }
    
    public static void writeToFile(File file, String content, boolean append) throws IOException {
        StandardOpenOption option = StandardOpenOption.CREATE;
        if(append && file.exists()) {
            option = StandardOpenOption.APPEND;
            content = "\n" + content;
        }
        
        Files.write(Paths.get(file.toURI()), content.getBytes(), option);
    }    
}
