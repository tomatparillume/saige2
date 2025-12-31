/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.input;

import com.parillume.print.bargraph.BarGraphData;
import com.parillume.util.FileUtil;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Reads a *_bargraph.xlsx file and writes a bar graph image per row in that file.
 * The table format in that file is assumed to follow this example:
 * <pre>
        COLUMN     COLUMN       COLUMN        COL.   COL.   COL.   COL.
   ROW: imagename  bar widths    bar height   % 1    % 2    % 3    % 4
                              (60 by default)
   ROW: name1      960, 576                   0      20     63     17
   ROW: name2      576                        22     10     18     50
 * </pre>
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class BarGraphDataImporter {

    private File barGraphSheetFile;
    
    public BarGraphDataImporter(File barGraphSheetFile) {
        setBarGraphSheetFile(barGraphSheetFile);
    }
    
    public List<BarGraphData> read() throws Exception {
        List<BarGraphData> barGraphData = new ArrayList<>();
    
        InputStream inputStream = FileUtil.toStream(getBarGraphSheetFile());
        if(inputStream == null)
            throw new Exception(getBarGraphSheetFile().getAbsolutePath() + " not found");
        
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                                
        XSSFSheet worksheet = workbook.getSheetAt(0);
        
        OUTER: for(int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++) {
            Row row = worksheet.getRow(rowIndex);
            if(row == null)
                break;
            
            String rowErrorPrefix = "Row #"+(rowIndex+1);
            
            String barFileName = null;
            // Bar widths for one or two bars; usually 960 or 576
            int[] barWidths = new int[2]; 
            Integer barHeight = 60; // by default
            
            int[] barPercentages = new int[4];
            
            // Seven cells: bar file name, bar width, bar height, and 4 bar percentages
            for(int cellIndex = 0; cellIndex < 7; cellIndex++) {
                String cellErrorPrefix = rowErrorPrefix + ", cell #"+(cellIndex+1);
                
                Cell cell = row.getCell(cellIndex);
                
                if(cellIndex > 6) {
                    break OUTER;   
                    
                } else if(cellIndex == 2 && // the barHeight cell
                          (cell == null || CellType.BLANK == cell.getCellType())
                        ) {
                    // The barHeight cell is empty; we will use the default barHeight
                    continue;
                }
                
                if(CellType.STRING == cell.getCellType()) {
                    if(cellIndex == 0) {
                        barFileName = cell.getStringCellValue();

                    } else if(cellIndex == 1) { 
                        // Two comma-separated barWidths are defined
                        String val = cell.getStringCellValue();
                        barWidths = Arrays.stream(val.split("\\s*,\\s*"))
                                          .mapToInt(Integer::parseInt)
                                          .toArray(); 

                    } else {
                        throw new Exception(cellErrorPrefix + ": string values are not allowed in this cell");
                    }
                    
                } else if(CellType.NUMERIC == cell.getCellType()) {
                    int val = (int) cell.getNumericCellValue();
                    switch(cellIndex) {
                        case 1: 
                            // Only one barWidth is defined
                            barWidths = new int[]{val};
                            break;
                        case 2: 
                            barHeight = val; 
                            break;
                        default: 
                            // cellIndex 3 is the first (0-index) barPercentage:
                            barPercentages[cellIndex-3] = (int) cell.getNumericCellValue();
                    }
                    
                } else {
                    throw new Exception(cellErrorPrefix + ": " + cell.getCellType().name() + " is not a valid cell value type");
                }
            }
            
            if(barFileName == null)
                throw new Exception(rowErrorPrefix + ": No imagename value was provided");
            if(Arrays.stream(barPercentages).sum() != 100)
                throw new Exception(rowErrorPrefix + ": Percentages do not sum to 100");
            
            barGraphData.add(
                new BarGraphData(barFileName, barWidths, barHeight, barPercentages)
            );
        }     
        
        return barGraphData;
    }
}
