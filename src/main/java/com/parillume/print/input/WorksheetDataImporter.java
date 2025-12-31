/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.input;

import com.parillume.model.external.User;
import com.parillume.model.score.CSStrengthScore;
import com.parillume.model.score.EnneagramScore;
import com.parillume.model.score.MBPreferenceScore;
import com.parillume.model.score.MBTypeScore;
import com.parillume.util.FileUtil;
import com.parillume.util.StringUtil;
import com.parillume.util.webapp.WebAppDisplayUtil;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Import worksheet data from files on disk or from a List of Users.
 * 
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class WorksheetDataImporter {
    // One of these will be populated during import
    // Values in worksheetFiles may be null
    private Map<File,InputStream> worksheetFiles = new HashMap<>();
    private List<User> sheetUsers = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    
    private Map<DataImportKeyIF, String[]> sheetNames = new HashMap<>();
    private Map<DataImportKeyIF, String[]> allNames = new HashMap<>();
    private Map<DataImportKeyIF, List<Integer>> strengthRows = new HashMap<>();
    private Map<DataImportKeyIF, Map<Integer,CSStrengthScore>> rowNumToStrength = new HashMap<>();
    private Map<DataImportKeyIF, List<CSStrengthScore>> strengths = new HashMap<>();
    
    private Map<DataImportKeyIF, List<MBPreferenceScore>> preferences = new HashMap<>();
    private Map<DataImportKeyIF, Integer> enneagram = new HashMap<>();
    
    private Map<DataImportKeyIF, List<String>> superpowers = new HashMap<>();
    private Map<DataImportKeyIF, List<String>> kryptonite = new HashMap<>();
    private Map<DataImportKeyIF, List<String>> PFPs = new HashMap<>();
    
    private Map<DataImportKeyIF, String> userId = new HashMap<>();
    private Map<DataImportKeyIF, String> emailAddress = new HashMap<>(); // Username
    private Map<DataImportKeyIF, String> password = new HashMap<>();
    
    // Display names disambiguate people with the same name:
    //      e.g. file1:"Jane D", "Jane Ef", "Jane Em" ...
    // The TeamChartsWriter sets these fileName:displayName values when 
    // looping through the worksheets.
    private Map<DataImportKeyIF, String> displayNames = new HashMap<>();
        
    public WorksheetDataImporter(User[] sheetUsers, User[] allUsers) 
    throws Exception {
        setSheetUsers( new ArrayList<>(Arrays.asList(sheetUsers)) );        
        setAllUsers(new ArrayList<>(Arrays.asList(allUsers)) );        
        WebAppDisplayUtil.sortUsersByName(getSheetUsers());        
        importUserData();
    }
    
    /**
     * These two constructors assume that the database will not be inspected for
     * pre-existing users.
     */
    public WorksheetDataImporter(List<File> worksheetFiles) 
    throws Exception {
        Map<File,InputStream> map = worksheetFiles.stream().collect(Collectors.toMap(f->f, f->FileUtil.toStream(f)));
        initialize(map);
    }
    public WorksheetDataImporter(Map<File,InputStream> worksheetFiles) throws Exception {
        initialize(worksheetFiles);
    }
    /**
     * This assumes the database will not be inspected for pre-existing users, 
     * and thus assigns the imported user names as "all" names (rather than
     * reading "all" names from the database).
     */
    private void initialize(Map<File,InputStream> worksheetFiles) throws Exception {
        Map<File,InputStream> sortedMap = 
                worksheetFiles.entrySet()
                              .stream()
                              .sorted(Map.Entry.comparingByKey(Comparator.comparing(File::getName)))
                              .collect(Collectors.toMap(
                                       Map.Entry::getKey, 
                                       Map.Entry::getValue, 
                                       (e1, e2) -> e1, 
                                       LinkedHashMap::new) );
        
        setWorksheetFiles(sortedMap);
        
        importFileData();
        
        setAllNames(getSheetNames());
    }
    
    public List<DataImportKeyIF> getKeys() {
        return new ArrayList( getSheetNames().keySet() );                                   
    }
    
    private void importUserData() throws Exception {
        for(User user: getSheetUsers()) {
            readUser(user);
        }        
        for(User user: getAllUsers()) {
            getAllNames().put(new UserImportKey(user), new String[]{user.getNameFirst(), user.getNameLast()});
        }
    }
    
    private void importFileData() throws Exception {
        Set<String> fileNames = new HashSet<>();
        
        for(File f: getWorksheetFiles().keySet()) {
            if(!fileNames.add(f.getName()))
                throw new Exception("Duplicate file names ("+f.getName()+") are not allowed");
            
            readFile(f, getWorksheetFiles().get(f));
        }
        
        List<String> duplicateFullNames = StringUtil.getDuplicateValues(
                        new ArrayList( 
                            getSheetNames().entrySet()
                                      .stream() // join => "Firstname Lastname"
                                      .map(e -> String.join(" ", e.getValue()) )
                                      .collect(Collectors.toList()) 
                        )
        );        
        if(!duplicateFullNames.isEmpty())
            throw new Exception("Multiple worksheets have the same name entries: " + duplicateFullNames);
    } 
    
    public void initDisplayNames(boolean firstNameOnly) {
        if(!firstNameOnly) {
            displayNames = getSheetNames().entrySet()
                                     .stream()
                                     .collect( 
                                        Collectors.toMap(e -> e.getKey(), 
                                                         // "Jane Smith":
                                                         e -> getFirstName(e.getKey()) + " " + getLastName(e.getKey()) )
                                     );
            return;
        }
        
        displayNames = getAllNames().entrySet()
                                    .stream()
                                    .collect( 
                                       Collectors.toMap(e -> e.getKey(), 
                                                        // "Jane":
                                                        e -> getAllNames().get(e.getKey())[0] )
                                    );
        
        int lastNameLetterIndex = 1;        
        boolean uniqueness = false;
        while(!uniqueness) {
            uniqueness = disambiguateDisplayNames(lastNameLetterIndex++);            
        } 
        
        // Remove from displayNames those that do not represent sheet users
        displayNames.keySet().removeIf(key -> !getSheetNames().containsKey(key));
    }
    
    // If display names are first-name only, we may have to add letters from the
    // last names to disambiguate them.
    private boolean disambiguateDisplayNames(int lastNameLetterIndex) {             
        List<String> duplicateDisplayNames = StringUtil.getDuplicateValues(new ArrayList(displayNames.values()));
        if(duplicateDisplayNames.isEmpty())
            return true;
          
        for(DataImportKeyIF key: displayNames.keySet()) {
            String displayName = displayNames.get(key);            
            if(duplicateDisplayNames.contains(displayName)) {
                String firstName = getAllNames().get(key)[0];
                String lastName = getAllNames().get(key)[1];
                // "Jane", "Jane E", Jane Em" ...
                displayName =  firstName + " " + lastName.substring(0,lastNameLetterIndex);
                displayNames.put(key, displayName);
            }
        }
        
        return false;
    }
    public String getDisplayName(DataImportKeyIF key) {
        return getDisplayNames().get(key);
    }
    public void setDisplayName(DataImportKeyIF key, String displayName) {
        getDisplayNames().put(key, displayName);
    }
    
    public String getFullName(DataImportKeyIF key) {
        return getFirstName(key) + " " + getLastName(key);
    }
    
    public String getFirstName(DataImportKeyIF key) {
        return getSheetNames().get(key)[0];
    }
    
    public String getLastName(DataImportKeyIF key) {
        return getSheetNames().get(key)[1];
    }

    private void readUser(User user) throws Exception {
        DataImportKeyIF userKey = new UserImportKey(user);
        
        userId.put(userKey, user.getId());
        emailAddress.put(userKey, user.getEmailAddress());
        password.put(userKey, user.getPassword());
        
        sheetNames.put(userKey, new String[]{user.getNameFirst(), user.getNameLast()});
        
        superpowers.put(userKey, user.getSuperpowers());
        kryptonite.put(userKey, user.getKryptonite());
        PFPs.put(userKey, user.getPlayfullPractices());
        
        List<CSStrengthScore> userStrengths = new ArrayList<>();
        List<MBPreferenceScore> userPrefs = new ArrayList<>();
        for(String id: user.getAssessmentResultIds()) {
            CSStrengthScore strength = CSStrengthScore.getStrengthById(id);
            if(strength != null) {
                userStrengths.add(strength);
                continue;
            }
            
            MBTypeScore mb = MBTypeScore.getScoreById(id);
            if(mb != null) {
                userPrefs = Arrays.asList(mb.getPreferences());
                continue;
            }
            
            EnneagramScore enn = EnneagramScore.getScoreById(id);
            if(enn != null) {
                enneagram.put(userKey, enn.getType());
            }
        }
        
        strengths.put(userKey, userStrengths);
        preferences.put(userKey, userPrefs);        
    }

    private void readFile(File file, InputStream inputStream) throws Exception {
        if(inputStream == null)
            throw new Exception(file.getAbsolutePath() + " not found");
        
        DataImportKeyIF fileKey = new FileImportKey(file);
        
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                                
        XSSFSheet worksheet = workbook.getSheetAt(0);
        
        for(int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++) {
            Row row = worksheet.getRow(rowIndex);
            if(row == null)
                continue;
            
            assignName(fileKey, row.getCell(0));            
            addStrength(fileKey, row.getCell(1), (rowIndex+1));
            addMyersBriggs(fileKey, row.getCell(2));
            assignEnneagram(fileKey, row.getCell(3));
            
            addSuperpowers(fileKey, row.getCell(4));
            addKryptonite(fileKey, row.getCell(5));
            addPFPs(fileKey, row.getCell(6));
            
            assignUserId(fileKey, row.getCell(7));
            
            assignEmailUsername(fileKey, row.getCell(8));
            assignPassword(fileKey, row.getCell(9));
            
            if(rowIndex > 42)
                break;
        }    
        
        if(StringUtil.isEmpty(getLastName(fileKey))) {
            throw new Exception(fileKey + " is missing a last-name entry; the 'Name' column " +
                                "must contain first name in row #1 and last name in row #2");
        }

        populateStrengths(fileKey);
    }
    
    private List initList(DataImportKeyIF key, Map map) {
        List preexistingList = (List) map.get(key);
        if(preexistingList == null) {
            preexistingList = new ArrayList<>();
            map.put(key, preexistingList);
        }
        return preexistingList;
    }
    private void populateStrengths(DataImportKeyIF key) {
        for(int rowNum: strengthRows.get(key)) {
            Map<Integer,CSStrengthScore> map = rowNumToStrength.get(key);
            CSStrengthScore strength = map.get(rowNum);
            initList(key, strengths).add(strength);
        }
    }    
    private void assignName(DataImportKeyIF key, Cell cell) {
        if(cell == null) return;
        
        String name = cell.getStringCellValue();
        if(StringUtil.isEmpty(name)) 
            return;
        
        String[] firstLastName = sheetNames.get(key);
        if(firstLastName == null) {
            firstLastName = new String[]{"",""};
            sheetNames.put(key, firstLastName);
        }
        
        if(StringUtil.isEmpty(firstLastName[0]))
            firstLastName[0] = name.trim();
        else 
            firstLastName[1] = name.trim();
    }
    private void addStrength(DataImportKeyIF key, Cell cell, int rowNum) {
        if(cell == null) return;
        if(CellType.NUMERIC == cell.getCellType()) {
            initList(key, strengthRows).add( (int) cell.getNumericCellValue() );
        } else {
            String strengthStr = cell.getStringCellValue();
            if(StringUtil.isEmpty(strengthStr))
                return;
            
            CSStrengthScore strength = null;
            try {
                strength = CSStrengthScore.valueOf(strengthStr.toUpperCase().trim());
            } catch(IllegalArgumentException exc) {
                return; // This row does not contain a valid CSStrengthScore
            }
            
            Map<Integer,CSStrengthScore> map = rowNumToStrength.get(key);
            if(map == null) {
                map = new HashMap<>();
                rowNumToStrength.put(key, map);
            }
            map.put(rowNum, strength);
        }
    }
    private void addMyersBriggs(DataImportKeyIF key, Cell cell) {
        if(cell == null) return;
        String initial = cell.getStringCellValue();
        if(!StringUtil.isEmpty(initial)) {
            initList(key, preferences).add( MBPreferenceScore.getPreferenceByLetter(initial) );
        }
    }
    private void assignEnneagram(DataImportKeyIF key, Cell cell) {
        if(cell == null ||
           CellType.NUMERIC != cell.getCellType()) {
            return;
        }

        enneagram.put(key, (int) cell.getNumericCellValue());
    }
    private void addSuperpowers(DataImportKeyIF key, Cell cell) {
        addLines(key, cell, superpowers);
    }
    private void addKryptonite(DataImportKeyIF key, Cell cell) {
        addLines(key, cell, kryptonite);
    }
    private void addPFPs(DataImportKeyIF key, Cell cell) {
        addLines(key, cell, PFPs);
    }
    private void assignUserId(DataImportKeyIF key, Cell cell) {
        if(cell == null) return;
        String id = cell.getStringCellValue();
        if(StringUtil.isEmpty(id))
            return;        
        userId.put(key, id);
    }
    private void assignEmailUsername(DataImportKeyIF key, Cell cell) {
        if(cell == null) return;
        String id = cell.getStringCellValue();
        if(StringUtil.isEmpty(id))
            return;        
        emailAddress.put(key, id);
    }
    private void assignPassword(DataImportKeyIF key, Cell cell) {
        if(cell == null) return;
        String id = cell.getStringCellValue();
        if(StringUtil.isEmpty(id))
            return;        
        password.put(key, id);
    }
    private void addLines(DataImportKeyIF key, Cell cell, Map<DataImportKeyIF, List<String>> map) {
        if(cell == null) return;
        String v = cell.getStringCellValue();
        if(StringUtil.isEmpty(v))
            return;
        
        initList(key, map).addAll( Arrays.asList(v.split("\\r?\\n")) );
    }
}
