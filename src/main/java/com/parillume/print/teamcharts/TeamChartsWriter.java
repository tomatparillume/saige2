/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.teamcharts;

import com.parillume.util.content.TextLine;
import com.parillume.print.display.DiskImage;
import com.parillume.print.display.PDFImageWriter;
import com.parillume.print.output.AbstractPDFWriter;
import com.parillume.print.teamcharts.display.TeamChartsEnnScoreDisplay;
import com.parillume.print.teamcharts.display.TeamChartsLogoDisplay;
import com.parillume.print.teamcharts.display.TeamChartsMBAnimalsDisplay;
import com.parillume.print.teamcharts.display.TeamChartsMBPrefDisplay;
import com.parillume.print.teamcharts.display.TeamChartsNameDisplay;
import com.parillume.print.teamcharts.display.TeamChartsStrengthCircleDisplay;
import com.parillume.print.teamcharts.display.TeamChartsTeamNameDisplay;
import com.parillume.model.score.CSStrengthScore;
import com.parillume.model.score.EnneagramScore;
import com.parillume.model.score.MBPreferenceScore;
import com.parillume.model.score.MBTypeScore;
import com.parillume.print.PrintWriterIF;
import com.parillume.print.ProcessArg;
import com.parillume.print.display.DBImage;
import com.parillume.print.display.ImageIF;
import com.parillume.print.display.MultipartImage;
import com.parillume.print.input.DataImportKeyIF;
import com.parillume.print.input.WorksheetDataImporter;
import com.parillume.service.ImageService;
import com.parillume.util.FileUtil;
import com.parillume.util.StringUtil;
import com.parillume.util.model.ImageType;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 * Writes text to team worksheets.
 * 
 * @author tmargolis
 * @author tom@parillume.com
 * See https://www.tutorialspoint.com/javaexamples/add_text_to_pdf.htm
 */
@Data
public class TeamChartsWriter extends AbstractPDFWriter implements PrintWriterIF {
    
    private String teamName;
    private String companyId;
    private ImageService imageService;
    
    public TeamChartsWriter() throws Exception {}
    
    public TeamChartsWriter(WorksheetDataImporter importer, String teamName) 
    throws Exception {
        this(importer, teamName, null, null);
    }
    
    public TeamChartsWriter(WorksheetDataImporter importer, String teamName,
                            ImageService imageService, String companyId) 
    throws Exception {
        super(importer);
        setTeamName(teamName);
        
        setImageService(imageService);
        setCompanyId(companyId);
    }
    
    @Override
    public Integer getMaxUsers(ProcessArg process) {
        int teamSize = 2;
        while(true) {
            File pdfTemplate = getPDFTemplate(teamSize);
            setDoc(null);
            try {                
                init(pdfTemplate);
                teamSize++;
            } catch(Exception exc) {
                return teamSize-1;
            }
        }
    }
    
    @Override
    public void write() throws Exception {                
        generate();         
        close(true);
    }
    
    /**
     * Returns a list with only one entry, representing the single team chart
     */
    @Override    
    public Map<String, byte[]> getBytes() throws Exception {
        generate();
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        getDoc().save(byteArrayOutputStream);
        close(false); 
        
        Map<String,byte[]> map = new HashMap<>();
        map.put(getTeamName(), byteArrayOutputStream.toByteArray());
        return map;
    }
    
    private File getPDFTemplate(int teamSize) {
        return getPDFTemplate(teamSize + "_teamcharts.pdf");
    }
    
    private void generate() throws Exception {
        Collection<DataImportKeyIF> keys = getImporter().getKeys();
        int teamSize = keys.size();
        if(teamSize == 0)
            throw new Exception("No users found to create team chart");
        
        File pdfTemplate = getPDFTemplate(teamSize);
        setOutputFile( new File(FileUtil.TMP_DIR, pdfTemplate.getName()) );                       
        init(pdfTemplate);

        writeLogos();
        
        getImporter().initDisplayNames(true);
        
        Map<String,DataImportKeyIF> displayNameToWorksheetFileName =
            new TreeMap( // TreeMaps are ordered by key (i.e. by displayName)
                getImporter().getDisplayNames().entrySet()
                                               .stream()
                                               .collect( Collectors.toMap(e->e.getValue(), e->e.getKey()) )
            );
        
        // Re-acquire the ORDERED keys and display names
        keys = displayNameToWorksheetFileName.values();
        Collection<String> displayNames = displayNameToWorksheetFileName.keySet();
        
        writeMyersBriggsNames(displayNames, teamSize);
        
        writeTeamName(teamSize);
        
        writeMyersBriggsPreferences(keys, teamSize);
        
        writeMyersBriggsAnimals(keys, teamSize);
        
        writeEnneagramScores(keys, teamSize);
        
        // Some PDFs have Strengths on a second page:
        if(getDoc().getNumberOfPages() > 1) {
            setPage( getDoc().getPage(1) );
            writeTeamName(teamSize);
            writeLogos();
        }
        
        writeStrengthNames(displayNames, teamSize);
        writeStrengthCircles(keys, teamSize);
    }
    
    private ImageIF getClientLogo() {
        // If triggered by WorksheetController (i.e. from the command line) rather
        // than from SheetsController (i.e. from the UI), companyId will be null.
                
        // Triggered from the UI: look for dbLogo in the database
        if( !StringUtil.isEmpty(getCompanyId()) ) {
            DBImage dbLogo = getImageService().getImage(getCompanyId(), ImageType.LOGO);
            return dbLogo != null ?
                   new MultipartImage(ImageIF.CLIENT_LOGO, dbLogo.getImageBytes()) :
                   null;
            
        // Triggered from the command line: look for dbLogo in the /tmp directory
        } else {
            return DiskImage.getImage(ImageIF.CLIENT_LOGO);
        }

    }
    private void writeLogos() throws Exception {
        ImageIF optionalClientLogo = getClientLogo();
        
        PDPageContentStream contentStream = createContentStream();
        PDFImageWriter.drawLogo(DiskImage.getImage(ImageIF.PARILLUME_LOGO),
                              getPage(), getDoc(), contentStream, 
                              TeamChartsLogoDisplay.getInstance());
        
        if(optionalClientLogo != null) {
            PDFImageWriter.drawLogo(optionalClientLogo,
                                 getPage(), getDoc(), contentStream, 
                                 TeamChartsLogoDisplay.getInstance());
        }
        
        contentStream.close();        
    }
    
    private void writeTeamName(int teamSize)
    throws Exception {
        TeamChartsTeamNameDisplay display = TeamChartsMBEnnDisplayFactory.getTeamNameDisplay(teamSize);
        
        float pageWidth = getPage().getMediaBox().getWidth();
        float pageHeight = getPage().getMediaBox().getHeight();
        
        float centeredX = center( getTeamName(), getMontserratRegular(), display.getSize(), 
                                  (pageWidth/2) );
       
        TextLine line = new TextLine(getMontserratRegular(), display.getSize(), 
                                     centeredX, pageHeight - display.getYAdjustment(), 
                                     getTeamName());
        writeText(line);        
    }
    
    private void writeMyersBriggsNames(Collection<String> displayNames, int teamSize) 
    throws Exception {
        TeamChartsNameDisplay display = TeamChartsMBEnnDisplayFactory.getNameDisplay(teamSize);
        writeNames(displayNames, teamSize, display);
    }   
    
    private void writeMyersBriggsPreferences(Collection<DataImportKeyIF> keys, int teamSize) 
    throws Exception {
        TeamChartsMBPrefDisplay prefDisplay = TeamChartsMBEnnDisplayFactory.getPreferencesDisplay(teamSize);
                
        // Each worksheet represents a different person's row; we adjust 
        // vertically as we loop through these rows.
        float y = prefDisplay.getY();
        
        /*** Loop through Myers-Briggs lines - one per worksheet ***/
        int lineNum = 1;  
        for(DataImportKeyIF key: keys) {
            // e.g. List<I, N, T, J>
            List<MBPreferenceScore> preferenceScores = getImporter().getPreferences().get(key);
            
            /*** Loop through preferences: I, N, T, J ***/
            int prefX = prefDisplay.getX(); // The starting X for this line
            for(MBPreferenceScore score: preferenceScores) {                
                TextLine line = new TextLine(getMontserratRegular(), 
                                             prefDisplay.adjustSize(score.getSymbol()), 
                                             prefX, y, score.getSymbol());
                writeText(line);
                
                // Add a space between each preference: I   N   T   J
                prefX = prefDisplay.adjustX(prefX);
            }
            
            // Adjust vertically before writing the next M-B line
            y = prefDisplay.adjustY(y, lineNum++);    
        }
    }        
    
    private void writeMyersBriggsAnimals(Collection<DataImportKeyIF> keys, int teamSize) 
    throws Exception {
        TeamChartsMBAnimalsDisplay animalsDisplay = TeamChartsMBEnnDisplayFactory.getAnimalsDisplay(teamSize);
                
        int x = animalsDisplay.getX();
        float y = animalsDisplay.getY();     
        
        int lineNum = 1;    
        for(DataImportKeyIF key: keys) {
            List<MBPreferenceScore> preferenceScores = getImporter().getPreferences().get(key);
            MBTypeScore typeScore = MBTypeScore.getType(preferenceScores);
            
            String animal = typeScore.getAnimal();
            int fontSize = animalsDisplay.adjustSize(animal);
            float centeredX = center(animal, getMontserratExtraLight(), fontSize, x);
        
            TextLine line = new TextLine(getMontserratRegular(), 
                                         fontSize, 
                                         centeredX, y, animal);
            writeText(line);
            
            x = animalsDisplay.adjustX(x);
            y = animalsDisplay.adjustY(y, lineNum++);              
        }
    }   
        
    private void writeEnneagramScores(Collection<DataImportKeyIF> keys, int teamSize) 
    throws Exception {
        TeamChartsEnnScoreDisplay ennDisplay = TeamChartsMBEnnDisplayFactory.getEnneagramDisplay(teamSize);
                
        int x = ennDisplay.getX(); 
        float labelY = ennDisplay.getLabelY();
        float bqY = ennDisplay.getBurningQuestionY();    
        
        int lineNum = 1;    
        for(DataImportKeyIF key: keys) {
            int ennType = getImporter().getEnneagram().get(key);
            EnneagramScore ennScore = EnneagramScore.getScoreByType(ennType);
            
            
            String label = ennScore.getLabel();
            String labelDisplay = ennType + ": " + label;
            int labelFontSize = ennDisplay.adjustSize(label);
            float centeredLabelDisplayX = center(labelDisplay, getMontserratExtraLight(), 
                                                 labelFontSize, x);
        
            TextLine labelLine = new TextLine(getMontserratRegular(), 
                                              labelFontSize, 
                                              centeredLabelDisplayX, labelY, 
                                              labelDisplay);
            writeText(labelLine);
            
            
            String burningQuestion = ennScore.getBurningQuestion();
            int bqFontSize = ennDisplay.adjustSize(burningQuestion);
            float centeredBqX = center(burningQuestion, getMontserratExtraLight(), 
                                       bqFontSize, x);
            
            TextLine bqLine = new TextLine(getMontserratELItalic(), 
                                           bqFontSize, 
                                           centeredBqX, bqY, 
                                           burningQuestion);
            writeText(bqLine);
            
            x = ennDisplay.adjustX(x);
            labelY = ennDisplay.adjustY(labelY, lineNum++);              
            bqY = ennDisplay.adjustY(bqY, lineNum++);              
        }        
    }
    
    private void writeStrengthNames(Collection<String> displayNames, int teamSize) 
    throws Exception {
        TeamChartsNameDisplay display = TeamChartsStrengthsDisplayFactory.getStrengthNameDisplay(teamSize);
        writeNames(displayNames, teamSize, display);    
    }
    
    private void writeStrengthCircles(Collection<DataImportKeyIF> keys, int teamSize) 
    throws Exception {
        TeamChartsStrengthCircleDisplay display = TeamChartsStrengthsDisplayFactory.getStrengthCircleDisplay(teamSize);
        
        PDPageContentStream contentStream = createContentStream();
        
        float y = display.getY();   
        int lineNum = 1;    
        for(DataImportKeyIF key: keys) {
            // This person's scores:
            List<CSStrengthScore> strengthScores = getImporter().getStrengths().get(key);
            
            // All strengths, in their display order:
            List<CSStrengthScore> strengthsList = Arrays.asList(CSStrengthScore.values());
                     
            int strengthNum = 1; // 1-5
            for(CSStrengthScore score: strengthScores) {
                // strengthIndex places the circle under the proper display column;
                // e.g. Analytical is index 0; Maximizer is index 12
                int strengthIndex = strengthsList.indexOf(score);
                float x = display.getX(strengthIndex);
                
                PDFImageWriter.drawImage(DiskImage.getStrengthCircle(strengthNum), 
                                      getPage(), getDoc(), contentStream, 
                                      12f, 12f, x, y); 
        
                strengthNum++;
            }
            
            y = display.adjustY(y, lineNum++);
        }
          
        contentStream.close();
    }
        
    private void writeNames(Collection<String> displayNames, int teamSize,
                            TeamChartsNameDisplay display) 
    throws Exception {
        int x = display.getX();
        float y = display.getY();
        int lineNum = 1;    

        for(String name: displayNames) {              
            PDType0Font font = getMontserratLight();
            int fontSize = display.adjustSize(name);
            float centeredX = center(name, font, fontSize, x);             
            TextLine line = new TextLine(font, fontSize, centeredX, y, name);
            writeText(line);
            
            x = display.adjustX(x);
            y = display.adjustY(y, lineNum++);       
        }        
    }
}