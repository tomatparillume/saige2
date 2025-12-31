/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.util.StringUtil;
import java.util.Comparator;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class TableCellLineDTO {
    
    public static final LineComparator LINE_COMPARATOR = new LineComparator();
    
    /**
     * label represents an internal header within cell-row text:
     * {label:8, text:Heroic leadership} yields this cell text...
     *      "8
     *       Heroic leadership"
     */    
    private String label;
    private String text;
    
    public TableCellLineDTO() {}
    
    public TableCellLineDTO(String single) {
        this(null, single);
    }   
    
    public TableCellLineDTO(String label, String text) {
        setLabel(label);
        setText(text);
    }
    
    private static class LineComparator implements Comparator<TableCellLineDTO> {
        @Override
        public int compare(TableCellLineDTO o1, TableCellLineDTO o2) {
            String label1 = o1.getLabel();
            String label2 = o2.getLabel();
            if(StringUtil.isEmpty(label1) || StringUtil.isEmpty(label2)) {
                if(!StringUtil.isEmpty(label1))
                    return 1;
                else if(!StringUtil.isEmpty(label2))
                    return -1;
                else
                    return 0;
            }
            
            return label1.compareTo(label2);
        }
    }
}
