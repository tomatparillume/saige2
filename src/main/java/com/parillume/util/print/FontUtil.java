/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.content;

import com.parillume.util.FileUtil;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class FontUtil {
    public enum AppFont {
        MONTSERRAT("montserrat", "Montserrat-Regular.ttf"),
        MONTSERRAT_BOLD("montserrat", "Montserrat-Bold.ttf"),
        MONTSERRAT_LIGHT("montserrat", "Montserrat-Light.ttf"),
        MONTSERRAT_EXTRALIGHT("montserrat", "Montserrat-ExtraLight.ttf"),
        MONTSERRAT_EXTRALIGHT_ITALIC("montserrat", "Montserrat-ExtraLightItalic.ttf");
        
        private String subdir;
        private String fileName;
        private AppFont(String subdir, String fileName) {
            this.subdir = subdir;
            this.fileName = fileName;
        }
        public File getFontFile() {
            return new File(new File(FileUtil.FONTS_DIR, subdir), fileName);
        }
        public InputStream getFontStream() {
            return getClass().getClassLoader().getResourceAsStream(subdir + "/" + fileName);
        }
    }
    
    public static PDType0Font load(PDDocument document, AppFont appFont) throws IOException {
        try {
            return PDType0Font.load(document, appFont.getFontFile());
        } catch(Exception exc) {
            return PDType0Font.load(document, appFont.getFontStream());
        }
    }
    
    public static Font load(AppFont appFont, float fontSize) throws Exception {
        Font font = Font.createFont(Font.TRUETYPE_FONT, appFont.getFontFile());
        return font.deriveFont(fontSize);
    }
}
