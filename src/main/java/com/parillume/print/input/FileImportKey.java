/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.input;

import java.io.File;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class FileImportKey implements DataImportKeyIF {
    private File file;
    
    public FileImportKey(File file) {
        setFile(file);
    }
    
    @Override
    public String getName() {
        return file.getName();
    }
    
    @Override    
    public String getOutputFileName() {
        return getName().split("\\.")[0];
    }
}
