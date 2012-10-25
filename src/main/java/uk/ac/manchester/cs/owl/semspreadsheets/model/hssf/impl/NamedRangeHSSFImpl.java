/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.ac.manchester.cs.owl.semspreadsheets.model.NamedRange;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public class NamedRangeHSSFImpl implements NamedRange {

    private WorkbookHSSFImpl workbook;

    private HSSFName name;

    public NamedRangeHSSFImpl(WorkbookHSSFImpl workbook, HSSFName name) {
        this.workbook = workbook;
        this.name = name;
    }

    public String getName() {
        return name.getNameName();
    }

    public Range getRange() {
        String sheetName = name.getSheetName();
        String formula = name.getRefersToFormula();
        AreaReference areaReference = new AreaReference(formula);
        CellReference firstCellReference = areaReference.getFirstCell();
        CellReference lastCellReference = areaReference.getLastCell();
        return new Range(workbook.getSheet(sheetName), firstCellReference.getCol(), firstCellReference.getRow(), lastCellReference.getCol(), lastCellReference.getRow());
    }
}
