/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class TableCellDTO {
    private String id;
    private List<TableCellLineDTO> lines = new ArrayList<>();
    
    public TableCellDTO() {}
    
    public TableCellDTO(String single) {
        this(single, new TableCellLineDTO(single));
    }   
    
    public TableCellDTO(String id, TableCellLineDTO... lines) {
        this( id, lines != null ? new ArrayList(Arrays.asList(lines)) : null );
    }
    
    public TableCellDTO(String id, List<TableCellLineDTO> lines) {
        setId(StringUtil.toId(id));
        if(lines != null)
            setLines(lines);
    }
}
