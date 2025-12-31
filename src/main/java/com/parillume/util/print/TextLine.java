/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.content;

import lombok.Data;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

@Data
public class TextLine {
    private PDType0Font font;
    private int fontSize;
    private float x;
    private float y;
    private String text;
            
    public TextLine(PDType0Font font, int fontSize, float x, float y, String text) {
        setFont(font);
        setFontSize(fontSize);
        setX(x);
        setY(y);
        setText(text);
    }

    public PDType0Font getFont() {
        return font;
    }

    public void setFont(PDType0Font font) {
        this.font = font;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}