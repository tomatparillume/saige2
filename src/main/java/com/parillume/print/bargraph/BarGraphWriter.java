/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.bargraph;

import com.parillume.print.input.BarGraphDataImporter;
import com.parillume.util.Constants;
import com.parillume.util.FileUtil;
import com.parillume.util.content.FontUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.Data;

/**
 * Writes text to a one-sheet PDF.
 * 
 * @author tmargolis
 * @author tom@parillume.com
 * See https://www.tutorialspoint.com/javaexamples/add_text_to_pdf.htm
 */
@Data
public class BarGraphWriter {
    
    private static final Map<Integer,Color> SURVEYMONKEY_COLORS = Map.of(
        1, Constants.SURVEYMONKEY_GREEN, // lowest-score bar color
        2, Constants.SURVEYMONKEY_BLUE,
        3, Constants.SURVEYMONKEY_YELLOW,
        4, Constants.SURVEYMONKEY_TEAL  // highest-score bar color
    );
    
    private BarGraphDataImporter importer;
    
    public BarGraphWriter() throws Exception {}
    
    public BarGraphWriter(BarGraphDataImporter importer)
    throws Exception {
        setImporter(importer);
    }
    
    public int write() throws Exception {
        List<BarGraphData> data = getImporter().read();
        
        // Order bar graphs from best to worst
        data.sort(new BarGraphComparator());
        
        Map<Integer,Integer> barWidthToSortNum = new HashMap<>();
        for(BarGraphData thisData: data) {
            write(thisData, barWidthToSortNum);
        }
        //TJMTJM Check in; put jar file in Google Drive
        return barWidthToSortNum.values()
                                .stream()
                                .mapToInt(Integer::intValue)  // Convert Integer to int
                                .sum();
    }

    public int write(BarGraphData data, Map<Integer,Integer> barWidthToSortNum) throws Exception {
        for(int barWidth: data.getBarWidths()) {        
            
            int section1Width = (int) Math.round(data.getBarPercentages()[0] / 100.0 * barWidth);
            int section2Width = (int) Math.round(data.getBarPercentages()[1] / 100.0 * barWidth);
            int section3Width = (int) Math.round(data.getBarPercentages()[2] / 100.0 * barWidth);
            // Define final section's width using previous section widths:
            int section4Width = barWidth - (section1Width + section2Width + section3Width); 
            
            BufferedImage image = new BufferedImage(barWidth, data.getBarHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();

            ////// FILL BAR SECTIONS WITH COLOR
            int section1X = fillSectionColor(g, section1Width, data.getBarHeight(), 0, 1);
            int section2X = fillSectionColor(g, section2Width, data.getBarHeight(), section1X, 2);
            int section3X = fillSectionColor(g, section3Width, data.getBarHeight(), section2X, 3);
            int section4X = fillSectionColor(g, section4Width, data.getBarHeight(), section3X, 4);
            
            ////// WRITE TEXT TO BAR SECTIONS
            Font font = FontUtil.load(FontUtil.AppFont.MONTSERRAT, 24f);
            g.setFont(font);
            g.setColor(Color.WHITE);
       
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);            
        
            int unused_vertStagger = writePercentageText(g, data.getBarPercentages()[0], 0, 
                                                  section1Width, data.getBarHeight(),
                                                  0);
            unused_vertStagger = writePercentageText(g, data.getBarPercentages()[1], section1X, 
                                              section2Width, data.getBarHeight(),
                                              unused_vertStagger);
            unused_vertStagger = writePercentageText(g, data.getBarPercentages()[2], section2X, 
                                              section3Width, data.getBarHeight(),
                                              unused_vertStagger);
            unused_vertStagger = writePercentageText(g, data.getBarPercentages()[3], section3X, 
                                              section4Width, data.getBarHeight(),
                                              unused_vertStagger);
            
            g.dispose();
            
            Integer sortNum = barWidthToSortNum.get(barWidth);
            if(sortNum == null) sortNum = 0;
            barWidthToSortNum.put(barWidth, ++sortNum);
            
            ImageIO.write(image, "png", new File(FileUtil.TMP_DIR, 
                                                 barWidth + "_" + sortNum + "_" + data.getBarFileName() + ".png")); 
        }
        
        return data.getBarWidths().length;
    }
    
    private int fillSectionColor(Graphics2D g, int sectionWidth, int barHeight, int sectionX, int colorCount) {
        if(sectionWidth > 0) {
            g.setColor(SURVEYMONKEY_COLORS.get(colorCount));
            g.fillRect(sectionX, 0, sectionWidth, barHeight);
            sectionX += sectionWidth;
        }
        return sectionX;
    }      
        
    /**
     * Returns 1 if the text was staggered to the upper left,
     * -1 if it was staggered to the lower left, and 0 if
     * it was not vertically staggered.
     */
    private int writePercentageText(Graphics2D g, int percentage, int barX, int sectionWidth, int barHeight,
                                    int unused_previousVerticalStagger) 
    throws Exception {
        if(percentage == 0)
            return 0;
        
        FontRenderContext frc = g.getFontRenderContext();
        
        TextLayout layout = new TextLayout(percentage + "%", g.getFont(), frc);
        Rectangle2D bounds = layout.getBounds();
        double textWidth = bounds.getWidth();
        double textHeight = bounds.getHeight();

        // If the percentage text will extend beyond the section boundary,
        // remove the "%" from the text.
        if(textWidth > sectionWidth) {
            layout = new TextLayout(percentage + "", g.getFont(), frc);
            bounds = layout.getBounds();
            textWidth = bounds.getWidth();
            textHeight = bounds.getHeight();
        }
                
        double x = barX + ((sectionWidth - textWidth) / 2) - bounds.getX();
        double y = ((barHeight - textHeight) / 2) - bounds.getY();
        
        int unused_verticalStagger = 0;
/*
        // If the percentage text will extend beyond the section boundary,
        // put the text in the upper- or lower-left corner so it won't overlap
        // with text from the next bar to the right.
        
        if(textWidth > sectionWidth) {
            x = barX + 3; // move to the left boundary
            
            if(previousVerticalStagger < 0) {
                y = textHeight+5; // stagger upwards
                verticalStagger = 1;
            } else {
                y = (barHeight-5) - (textHeight + bounds.getY())/2; // stagger downwards
                verticalStagger = -1;
            }
        }
*/
        layout.draw(g, (float)x, (float)y); 
        
        return unused_verticalStagger;
    }
}