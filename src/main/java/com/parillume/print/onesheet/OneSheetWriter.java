/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.onesheet;

import com.parillume.util.content.TextLine;
import com.parillume.print.onesheet.display.OneSheetStrengthDisplay;
import com.parillume.print.onesheet.display.OneSheetMBPrefDisplay;
import com.parillume.print.onesheet.display.OneSheetNameDisplay;
import com.parillume.print.output.AbstractPDFWriter;
import com.parillume.model.score.CSStrengthScore;
import com.parillume.model.score.MBPreferenceScore;
import com.parillume.print.PrintWriterIF;
import com.parillume.print.input.DataImportKeyIF;
import com.parillume.print.input.WorksheetDataImporter;
import com.parillume.util.Constants;
import com.parillume.util.FileUtil;
import com.parillume.util.StringUtil;
import com.parillume.util.content.CSContentUtil;
import com.parillume.util.content.CSContentUtil.LeadershipTheme;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

/**
 * Writes text to a one-sheet PDF.
 * 
 * @author tmargolis
 * @author tom@parillume.com
 * See https://www.tutorialspoint.com/javaexamples/add_text_to_pdf.htm
 */
@Data
public class OneSheetWriter extends AbstractPDFWriter implements PrintWriterIF {
    
    private boolean anatomySheet = false;
    
    public OneSheetWriter() throws Exception {}
    
    public OneSheetWriter(WorksheetDataImporter importer, boolean anatomySheet)
    throws Exception {
        super(importer);
        setAnatomySheet(anatomySheet);
    }
    
    protected String getPDFSuffix() {
        return isAnatomySheet() ? "_anatomy.pdf" : ".pdf";
    }    
    
    /**
     *Returns a List of one-sheet byte arrays.
     */
    @Override    
    public Map<String, byte[]> getBytes() throws Exception {
        return generate(false);
    }
    
    @Override
    public void write() throws Exception {
        generate(true);
    }

    /**
     * For !saveToDisk, this returns a List of one-sheet byte arrays.
     */
    private Map<String, byte[]> generate(boolean saveToDisk) throws Exception {
        // For !saveToDisk only:
        Map<String, byte[]> oneSheetByteArrays = new HashMap<>();
        
        getImporter().initDisplayNames(false);
        
        Map<String, Object> fileNameToOutput = writeFiles(saveToDisk);                
        
        // If the above wrote anatomy one-sheets, we now write standard one-sheets:
        if(isAnatomySheet()) {
            setAnatomySheet(false);

            Map<String, Object> stdOutputsMap = writeFiles(saveToDisk);       
            
            List<String> fileNames = new ArrayList<>(stdOutputsMap.keySet());
            Collections.sort(fileNames);
            
            for(String fileName: stdOutputsMap.keySet()) {
                PDFMergerUtility PDFMerger = new PDFMergerUtility();                
                File mergedFile = null; // For saveToDisk=true
                ByteArrayOutputStream out = new ByteArrayOutputStream();  // For saveToDisk=false
                
                if(saveToDisk) {
                    File stdOneSheet = (File) stdOutputsMap.get(fileName);
                    File anatomyOneSheet = (File) fileNameToOutput.get(fileName);

                    PDFMerger.addSource(stdOneSheet);
                    PDFMerger.addSource(anatomyOneSheet);
                    
                    String mergedFileName = stdOneSheet.getName();
                    mergedFileName = mergedFileName.substring(0, mergedFileName.length()-4) +
                                                                "_multisheet.pdf";
                    mergedFile = new File(stdOneSheet.getParentFile().getAbsolutePath(), mergedFileName);

                    PDFMerger.setDestinationFileName(mergedFile.getAbsolutePath());        
                    
                } else {
                    byte[] stdOneSheetBytes = (byte[]) stdOutputsMap.get(fileName);
                    byte[] anatomyOneSheetBytes = (byte[]) fileNameToOutput.get(fileName);

                    PDFMerger.addSource(new ByteArrayInputStream(stdOneSheetBytes));
                    PDFMerger.addSource(new ByteArrayInputStream(anatomyOneSheetBytes)); 
                    
                    PDFMerger.setDestinationStream(out);
                }
                        
                PDFMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
                
                if(saveToDisk) {
                    // Rename the new multisheet file to remove the "multisheet" suffix
                    File stdOneSheet = (File) stdOutputsMap.get(fileName);
                    stdOneSheet.delete();
                    
                    File anatomyOneSheet = (File) fileNameToOutput.get(fileName);
                    anatomyOneSheet.delete();
                    
                    mergedFile.renameTo(stdOneSheet);
                } else {
                    oneSheetByteArrays.put(fileName, out.toByteArray());
                }
            }
        }
        
        // Only relevant to !saveToDisk:
        return oneSheetByteArrays;
    }

    // Map keys: file names
    // Map values: files written to disk, or file byte arrays
    private Map<String,Object> writeFiles(boolean saveToDisk) throws Exception {
        Map<String,Object> fileNameToOutputs = new HashMap<>();
        
        for(DataImportKeyIF key: getImporter().getKeys()) {
            try {
                String displayName = getImporter().getDisplayName(key);
                
                Integer enneagramType = getImporter().getEnneagram().get(key);
                
                File pdfTemplate = getPDFTemplate(enneagramType);  
                try {              
                    init(pdfTemplate); 
                } catch(NullPointerException npe) {
                    throw new Exception(pdfTemplate.getName() + " is not available");
                }
        
                String outputFileName = key.getOutputFileName();
                setOutputFile( new File(FileUtil.TMP_DIR, 
                                        outputFileName.replaceAll(" ", "_") + getPDFSuffix()) );   
                                
                List<CSStrengthScore> strengths = getImporter().getStrengths().get(key);
                writeResults(displayName, strengths, 
                             getImporter().getPreferences().get(key));

                writeSuperpowersKryptonitePFPs(getImporter().getSuperpowers().get(key),
                                               getImporter().getKryptonite().get(key),
                                               getImporter().getPFPs().get(key));   
                
                writeStrengthsAnatomyDescriptions(strengths);
                
                if(!saveToDisk) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    getDoc().save(byteArrayOutputStream); 
                    fileNameToOutputs.put(outputFileName, byteArrayOutputStream.toByteArray());
                }
                
                close(saveToDisk);
                
                if(saveToDisk)
                    fileNameToOutputs.put(outputFileName, getOutputFile());
                
            } catch(Exception exc) {
                if(saveToDisk)
                    deletePDF(getOutputFile());
                
                throw new Exception("Failed to write one-sheet for " + 
                                    key.getName() + ": " + exc.getMessage());
            }
        } 
        
        return fileNameToOutputs;
    }
    
    public void writeSuperpowersKryptonitePFPs(List<String> superpowers, 
                                               List<String> kryptonite, 
                                               List<String> PFPs) 
    throws Exception {
        if(isAnatomySheet())
            return;
        
        if(superpowers != null && !superpowers.isEmpty())
            writeSupKryptPFP(17f, superpowers, "superpowers");
        
        if(kryptonite != null && !kryptonite.isEmpty())
            writeSupKryptPFP(275f, kryptonite, "kryptonite");
        
        if(PFPs != null && !PFPs.isEmpty())
            writeSupKryptPFP(533f, PFPs, "PFPs");
    }
    
    public void writeStrengthsAnatomyDescriptions(List<CSStrengthScore> strengths) 
    throws Exception {
        if(!isAnatomySheet())
            return;
                    
        float descriptionFontSize = OneSheetStrengthDisplay.getDescriptionFontSize();
        
        PDType0Font strengthFont = getMontserratBold();
        PDType0Font descriptionFont = getMontserratLight();
        
        int verticalBuffer = 0;
        for(CSStrengthScore strength: strengths) {
            OneSheetStrengthDisplay strengthDisplay = OneSheetStrengthDisplay.getDisplay(strength);
            
            float x = OneSheetStrengthDisplay.getDescriptionX();
            float y = OneSheetStrengthDisplay.getDescriptionY() - verticalBuffer;

            writeText(strengthFont, descriptionFontSize, x, y, strength.getLabel());
            appendTrademark(descriptionFontSize, x, y, strengthFont, strength.getLabel(), strengthDisplay.getTrademark()); 

            PDPageContentStream contentStream = createContentStream();   
            writeText(contentStream, descriptionFont, descriptionFontSize-1, x, y-10, strength.getDescription());
            contentStream.close();
            
            verticalBuffer += OneSheetStrengthDisplay.getDescriptionVerticalBuffer();
        }
    }
    
    public void writeResults(String name, 
                             List<CSStrengthScore> strengths, 
                             List<MBPreferenceScore> preferences) throws Exception {
        writeName(name);
        writeStrengths(strengths);
        writeMyersBriggs(preferences);
    }
    
    private File getPDFTemplate(int enneagramType) {
        String pdfTemplateName = enneagramType + "_onesheet" + getPDFSuffix();
        return getPDFTemplate(pdfTemplateName);                
    }    
    
    private void writeSupKryptPFP(float x, List<String> text, String errorPrefix) 
    throws Exception {      
        List<TextLine> textLinesToPopulate = new ArrayList<>();
        
        boolean done = false;
        int fontSize = 10;
        while(fontSize >= 6) {
            done = generateSupKryptPFPLines(text, x, fontSize, textLinesToPopulate);
            if(done)
                break;
            
            fontSize--;
        }
        
        if(!done)
            throw new Exception(errorPrefix + " could not be written: too many lines");
        
        for(TextLine textLine: textLinesToPopulate) {
            writeText(textLine);
        }
    }
    private boolean generateSupKryptPFPLines(List<String> text, float x, int fontSize,
                                             List<TextLine> textLinesToPopulate) throws Exception {        
        float y = 100;
        float totalHeight = 0;
        float lineHeight = getTextWidthHeight("X", getMontserratExtraLight(), fontSize)[1];
        float lineBreakHeight = getTextWidthHeight("\\n", getMontserratExtraLight(), fontSize)[1];
        int wrapWidth = 460/fontSize;
        
        for(String t: text) {
            String[] lines = WordUtils.wrap(t, wrapWidth, "\n", false).split("\\r?\\n");
            for(int i=0; i < lines.length; i++) {
                String prefix = i==0 ? "\u2022 " : "  ";
                String l = prefix + lines[i];
                
                if(i != 0) totalHeight += lineBreakHeight;
                totalHeight += lineHeight;
                if(totalHeight > 58) {
                    textLinesToPopulate.clear();
                    return false;
                }
                
                textLinesToPopulate.add( new TextLine(getMontserratExtraLight(), Math.round(fontSize), x, y, l) );
                y -= 10;
            }
        }       
        
        return true;
    }
    
    private void writeName(String name) throws Exception {
        OneSheetNameDisplay display = new OneSheetNameDisplay(isAnatomySheet());
        
        float x = center(name, getMontserratExtraLight(), display.getNameFontSize(), display.getNameX());
        writeText(getMontserratExtraLight(), display.getNameFontSize(), x, display.getNameY(), name);
    }
    
    private void writeStrengths(List<CSStrengthScore> strengths) throws Exception {        
        addStrengths(getMontserratExtraLight(), getMontserratELItalic(), strengths);        
        writeDominantTheme(getMontserratBold(), strengths);
    }
    
    private void writeMyersBriggs(List<MBPreferenceScore> preferences) throws Exception {
        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setNonStrokingAlphaConstant(0.7f);
        
        for(MBPreferenceScore preferenceScore: preferences) {
            OneSheetMBPrefDisplay display = getMBPrefDisplay(preferenceScore);
            
            PDPageContentStream contentStream = createContentStream();  
            contentStream.setGraphicsStateParameters(graphicsState);
            contentStream.setNonStrokingColor(Color.WHITE);
            
            float[] xywh = display.getXYWH(isAnatomySheet());
            contentStream.addRect(xywh[0], xywh[1], xywh[2], xywh[3]);
            contentStream.fill();
            contentStream.close();
        }   
        
        writeMBType(getMontserratBold(), preferences);      
    }
    
    private void addStrengths(PDType0Font strengthFont, PDType0Font themeFont, List<CSStrengthScore> strengths) 
    throws Exception {            
        float labelFontSize = OneSheetStrengthDisplay.getLabelFontSize(isAnatomySheet());
            
        int verticalBuffer = 0;
        for(CSStrengthScore strength: strengths) {
            OneSheetStrengthDisplay strengthDisplay = OneSheetStrengthDisplay.getDisplay(strength);
            
            float x = center(strengthDisplay.getLabel(), strengthFont, labelFontSize, OneSheetStrengthDisplay.getLabelX(isAnatomySheet()));
            float y = OneSheetStrengthDisplay.getLabelY(isAnatomySheet()) - verticalBuffer;

            writeText(strengthFont, labelFontSize, x, y, strength.getLabel());
            appendTrademark(labelFontSize, x, y, strengthFont, strength.getLabel(), strengthDisplay.getTrademark()); 

            PDPageContentStream contentStream = createContentStream();
            writeTheme(contentStream, strength, themeFont, y);
            contentStream.close();
            
            verticalBuffer += OneSheetStrengthDisplay.getLabelVerticalBuffer(isAnatomySheet());
        }
    }    
    
    private void writeDominantTheme(PDType0Font font, List<CSStrengthScore> strengths) 
    throws Exception {
        LeadershipTheme dominantTheme = CSContentUtil.getDominantTheme(strengths);          
        writeColumnHeader(font, dominantTheme.getDominantLabel(), OneSheetStrengthDisplay.getLabelX(isAnatomySheet()));
    }   
    
    private void writeMBType(PDType0Font font, List<MBPreferenceScore> preferences) 
    throws Exception {
        String type = String.join(" ", preferences.stream()
                                                  .map(p -> getMBPrefDisplay(p).getSymbol())
                                                  .collect(Collectors.toList()));    
        writeColumnHeader(font, type, OneSheetMBPrefDisplay.getColumnHeaderX(isAnatomySheet()));       
    }
    private OneSheetMBPrefDisplay getMBPrefDisplay(MBPreferenceScore preferenceScore) {
        try {
            return OneSheetMBPrefDisplay.getDisplay(preferenceScore);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void writeColumnHeader(PDType0Font font, String header, float x) 
    throws Exception {
        PDPageContentStream contentStream = createContentStream();
        contentStream.setNonStrokingColor(Constants.PARILLUME_PURPLE);
                  
        x = center(header, font, OneSheetStrengthDisplay.getColumnHeaderFontSize(isAnatomySheet()), x);
        
        writeText(contentStream, font, OneSheetStrengthDisplay.getColumnHeaderFontSize(isAnatomySheet()), 
                  x, OneSheetStrengthDisplay.getColumnHeaderY(isAnatomySheet()), header);
        
        contentStream.close();    
    }
    
    private void writeTheme(PDPageContentStream contentStream, CSStrengthScore score, PDType0Font font, float y) 
    throws Exception {
        LeadershipTheme theme = score.getTheme();
        
        for(String themeLabel: theme.getLabels()) {
            int fontSize = theme.getLabelFontSize(isAnatomySheet());
            int themeGap = theme.getThemeGap(isAnatomySheet());
            float x = center(themeLabel, font, fontSize, OneSheetStrengthDisplay.getLabelX(isAnatomySheet()));       
            writeText(contentStream, font, fontSize, x, y-themeGap, themeLabel);
            y -= 12;
        }        
    }
    
    private void appendTrademark(float fontSize, float x, float y, 
                                 PDType0Font font, 
                                 String text, String trademark) throws Exception {
        if(StringUtil.isEmpty(trademark))
            return;
        
        PDPageContentStream contentStream = createContentStream(); 
        
        Float[] widthAndHeight = getTextWidthHeight(text, font, fontSize);

        contentStream.setFont(font, fontSize/2);        
        contentStream.beginText(); 
        contentStream.newLineAtOffset(x + widthAndHeight[0], y + (widthAndHeight[1]/2)); 
        contentStream.showText(trademark); 
        contentStream.endText();  
        
        contentStream.close();
    }
}