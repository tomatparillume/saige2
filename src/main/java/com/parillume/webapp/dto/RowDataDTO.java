/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class RowDataDTO {
    private List<TableCellDTO> cells = new ArrayList<>();
    
    public void addCell(String id, TableCellLineDTO line) {
        if(line != null)
            addCellDTOs(id, new ArrayList(Arrays.asList(line)) );
    }
    
    public void addCell(String id, String... lines) {
        if(lines != null) 
            addCell(id, Arrays.asList(lines));
    }
    
    public void addCell(String id, List<String> lines) {
        if(lines != null) {
            addCellDTOs(id,
                        lines.stream()
                             .map(l -> new TableCellLineDTO(l))
                             .collect(Collectors.toList())
            );
        }
    }    
    
    public void addCellDTOs(String id, List<TableCellLineDTO> lines) {
        if(lines != null)
            cells.add( new TableCellDTO(id, lines) );
    }
}
