/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.parillume.model.CompanyModel;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;


/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class TableDTO extends AbstractWebAppDTO {
    private List<TableCellDTO> columns = new ArrayList<>();
    private List<RowDataDTO> rows = new ArrayList<>();
    
    public TableDTO() {}
    
    public TableDTO(CompanyModel companyModel) {
        setId(companyModel.getCompany().getId());
        setLabel(companyModel.getCompany().getName());
    }
    
    @JsonIgnore
    public void addRow(RowDataDTO row) {
        getRows().add(row);
    }
}
