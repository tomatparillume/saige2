/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.output;

import com.parillume.print.input.WorksheetDataImporter;
import lombok.Data;

@Data
public abstract class AbstractContentWriter {
    private WorksheetDataImporter importer;
    
    public AbstractContentWriter() {}
    
    public AbstractContentWriter(WorksheetDataImporter importer) {
        setImporter(importer);
    }    
    
    public abstract void write() throws Exception;
}