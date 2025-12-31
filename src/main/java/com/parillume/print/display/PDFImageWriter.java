/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.display;

import com.parillume.print.display.AbstractLogoDisplay.Position;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.util.IOUtils;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class PDFImageWriter {
    
    public static void drawImage(ImageIF image, PDPage page, PDDocument doc, PDPageContentStream stream,
                                 float w, float h, float x, float y) 
    throws Exception {
        try {
            byte[] byteArray = IOUtils.toByteArray(image.getImageStream());        
            PDImageXObject imageObj = PDImageXObject.createFromByteArray(doc, byteArray, image.getFileName());               
            stream.drawImage(imageObj, x, y, w, h);
            
        } catch(Exception exc) {
            throw new Exception("Image " + image.getFileName() + " could not be drawn: " + exc.getMessage());
        }
    }
    
    /**
     * @param image: The Image enum
     * @param imageDisplay: The display-specific configuration for this Image enum;
     *                      e.g. one-sheet configuration vs team chart configuration.
     */
    public static void drawLogo(ImageIF image, PDPage page, PDDocument doc, PDPageContentStream stream,
                                AbstractLogoDisplay imageDisplay) 
    throws Exception {
        InputStream in = null;
        try {
            in = image.getImageStream();
            byte[] byteArray = IOUtils.toByteArray(in);        
            PDImageXObject imageObj = PDImageXObject.createFromByteArray(doc, byteArray, image.getFileName());   

            float pageLeftX = page.getMediaBox().getLowerLeftX();
            float pageRightX = page.getMediaBox().getUpperRightX();
            float pageTopY = page.getMediaBox().getUpperRightY();
                    
            ////// ADJUST IMAGE SIZE
            float w = imageObj.getWidth();
            float h = imageObj.getHeight();

            float targetW = imageDisplay.getW(image.getImageId());
            float targetH = imageDisplay.getH(image.getImageId());

            if(w != targetW || h != targetH) {
                float targetVolume = 4100f;
                float currentVolume = w * h;
                while(currentVolume > targetVolume) {
                    float adjustment = (float)Math.sqrt(targetVolume) / (float)Math.sqrt(currentVolume);
                    w = w * adjustment;
                    h = h * adjustment;
                    currentVolume = w * h;
                }
            }
            
            ////// ADJUST IMAGE POSITION
            // x and y represent the top left corner of the image
            Position position = imageDisplay.getPosition(image.getImageId());
            float x = 0;
            float y = 0;
            float topBuffer = 10;
            float sideBuffer = 20;
            if(Position.TOP_LEFT == position) {
                x = pageLeftX + sideBuffer;
                y = pageTopY - h - topBuffer;
                
            } else if(Position.TOP_RIGHT == position) {
                x = pageRightX - w - topBuffer;
                y = pageTopY - h - topBuffer;
            }
            
            stream.drawImage(imageObj, x, y, w, h);
            
        } catch(NullPointerException npe) {
            throw new Exception("Image " + image.getFileName() + " could not be found");
            
        } catch(Exception exc) {
            throw new Exception("Image " + image.getFileName() + " could not be drawn: " +  exc.getMessage());
        } finally {
            if(in != null) in.close();
        }
    }    
}
