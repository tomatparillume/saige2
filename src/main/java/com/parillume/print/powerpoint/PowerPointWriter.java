/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.powerpoint;

import com.parillume.util.FileUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import lombok.Data;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class PowerPointWriter {
    public static void main(String[] TJMTJM) throws Exception {
        String teamName = "TEAMNAME TEAMNAME";
        String date = "01.23.2024";
        
        PowerPointWriter importer = new PowerPointWriter();
        File inputFile = importer.getTeamWorkshopInputStream();
        
        org.apache.poi.xslf.usermodel.XMLSlideShow slideShow = new org.apache.poi.xslf.usermodel.XMLSlideShow(new FileInputStream(inputFile));
        
        XSLFSlide slide1 = slideShow.getSlides().get(0);
        
        XSLFTextBox teamNameBox = slide1.createTextBox();
        teamNameBox.setAnchor(new Rectangle(0, 400, 960, 100));
        
        XSLFTextParagraph teamNamePg = teamNameBox.addNewTextParagraph();
        teamNamePg.setTextAlign(TextParagraph.TextAlign.CENTER);
        
        XSLFTextRun teamNameRun = teamNamePg.addNewTextRun();
        teamNameRun.setText(teamName);
        teamNameRun.setFontColor(Color.BLACK);
        teamNameRun.setFontFamily("Montserrat");
        teamNameRun.setFontSize(30D);
        
        XSLFTextRun dateRun = teamNamePg.addNewTextRun();
        dateRun.setText("\n" + date);
        dateRun.setFontColor(Color.BLACK);
        dateRun.setFontFamily("Montserrat");
        dateRun.setFontSize(24D);
        
        // Create a PNG image of the first teamcharts page
        File teamChartsPDF = new File(FileUtil.TMP_DIR, "22_teamcharts.pdf");
        PDDocument pdfDoc = PDDocument.load(teamChartsPDF);        
        PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);
        BufferedImage pdfPageImg = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
        
        // Draw a mask over the top and bottom section of the teamcharts PNG
        Graphics graphics = pdfPageImg.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 3500, 325);
        graphics.fillRect(0, 2400, 3500, 150);
        graphics.dispose();
        
        // Write the teamcharts PNG image to disk
        File teamChartsPNG = new File(FileUtil.TMP_DIR, "22_teamcharts_1.png");
        ImageIOUtil.writeImage(pdfPageImg, teamChartsPNG.getAbsolutePath(), 300);        
        pdfDoc.close();
                
        // Read the teamcharts PNG image from disk and add to the PowerPoint
        byte[] pngData = IOUtils.toByteArray(new FileInputStream(teamChartsPNG));
        XSLFPictureData pictureData = slideShow.addPicture(pngData, PictureData.PictureType.PNG);        
        XSLFPictureShape picture = slideShow.getSlides().get(4).createPicture(pictureData);
        picture.setAnchor(new Rectangle(75, -25, 800, 618));
        
        File outputFile = new File(FileUtil.TMP_DIR, 
                                   teamName.replaceAll(" ", "_") + "-workshop.pptx");
        FileOutputStream os = new FileOutputStream(outputFile);
        slideShow.write(os);
        os.close();
    }
            
    public File getTeamWorkshopInputStream() throws Exception {
        File ppTemplate = new File(FileUtil.RESOURCES_DIR, "team_workshop_template.pptx"); 
        if(ppTemplate.exists()) {
            return ppTemplate;
        } else { // e.g. If we are running the jar file with no local src/main/resources dir:
            //TJMTJM TEST THIS:
            URL resource = getClass().getClassLoader().getResource(ppTemplate.getName());
            return new File(resource.toURI());
        }              
    }
}
