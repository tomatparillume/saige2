/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.output;

import com.parillume.print.ProcessArg;
import com.parillume.print.input.WorksheetDataImporter;
import com.parillume.util.FileUtil;
import com.parillume.util.content.TextLine;
import com.parillume.util.content.FontUtil;
import java.io.File;
import java.io.InputStream;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

@Data
public abstract class AbstractPDFWriter extends AbstractContentWriter {
    
    // This will get continually redefined by the implementing class as it loops
    // through files:
    private File outputFile;
    
    private PDDocument doc;
    private PDPage page;  

    private PDType0Font montserratExtraLight;
    private PDType0Font montserratLight;
    private PDType0Font montserratBold;
    private PDType0Font montserratELItalic; 
    private PDType0Font montserratRegular; 
    
    public AbstractPDFWriter() {}
    
    public AbstractPDFWriter(WorksheetDataImporter importer) {
        super(importer); 
    }
    
    public File getPDFTemplate(String pdfTemplateName) {
        return new File(FileUtil.TEMPLATES_DIR, pdfTemplateName);        
    }
    
    protected void deletePDF(File f) {
        if(f == null)
            return;
        
        try {
            getDoc().close();
            f.delete();
        } catch(Exception exc) {
            System.out.println("Failed to delete " + getOutputFile().getName() + ": " + exc.getMessage());
        }
    }
    
    protected void init(File pdf) throws Exception {
        if(getDoc() != null)
            return;
        
        if(pdf.exists()) {
            setDoc( PDDocument.load(pdf) ); 
        } else { // e.g. If we are running the jar file with no local src/main/resources dir:
            InputStream in = getClass().getClassLoader().getResourceAsStream(pdf.getName());
            setDoc( PDDocument.load(in) ); 
        }
        
        setPage( getDoc().getPage(0) );

        setMontserratExtraLight(FontUtil.load(getDoc(), FontUtil.AppFont.MONTSERRAT_EXTRALIGHT));
        setMontserratLight(FontUtil.load(getDoc(), FontUtil.AppFont.MONTSERRAT_LIGHT));
        setMontserratBold(FontUtil.load(getDoc(), FontUtil.AppFont.MONTSERRAT_BOLD));
        setMontserratELItalic(FontUtil.load(getDoc(), FontUtil.AppFont.MONTSERRAT_EXTRALIGHT_ITALIC));    
        setMontserratRegular(FontUtil.load(getDoc(), FontUtil.AppFont.MONTSERRAT));
    }
    
    protected void writeText(TextLine textLine) throws Exception {
        writeText(textLine.getFont(), textLine.getFontSize(),
                  textLine.getX(), textLine.getY(),
                  textLine.getText());        
    }
    protected void writeText(PDType0Font font, float fontSize, float x, float y, String text) 
    throws Exception {
        PDPageContentStream contentStream = createContentStream();
        writeText(contentStream, font, fontSize, x, y, text);        
        contentStream.close(); 
    }

    protected void writeText(PDPageContentStream contentStream,
                             PDType0Font font,
                             float fontSize, float x, float y,
                             String text) 
    throws Exception {        
        contentStream.setFont(font, fontSize);        
        contentStream.beginText(); 
        contentStream.newLineAtOffset(x, y); 
        contentStream.showText(text); 
        contentStream.endText();        
    }
    
    protected Float[] getTextWidthHeight(String text, PDType0Font font, float fontSize) 
    throws Exception {
        Float textWidth = (font.getStringWidth(text) / 1000) * fontSize;
        Float textHeight = (font.getFontDescriptor().getCapHeight() / 1000) * fontSize; 
        return new Float[]{textWidth, textHeight};
    }
    
    protected float center(String text, PDType0Font font, float fontSize,
                           float origX) 
    throws Exception {
        Float[] widthAndHeight = getTextWidthHeight(text, font, fontSize);
        return origX - (widthAndHeight[0]/2);        
    }
    
    protected PDPageContentStream createContentStream() throws Exception {
        return new PDPageContentStream(getDoc(), getPage(), PDPageContentStream.AppendMode.APPEND, true, true); 
    }
    
    protected void close(boolean saveToDisk) throws Exception {
        if(saveToDisk)
            getDoc().save(getOutputFile());
        
        getDoc().close(); 
        setDoc(null);        
    }
}