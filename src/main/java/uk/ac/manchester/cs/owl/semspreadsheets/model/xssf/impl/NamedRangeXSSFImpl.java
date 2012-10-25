package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFName;

import uk.ac.manchester.cs.owl.semspreadsheets.model.NamedRange;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public class NamedRangeXSSFImpl implements NamedRange {

    private WorkbookXSSFImpl workbook;

    private XSSFName name;

    public NamedRangeXSSFImpl(WorkbookXSSFImpl workbook, XSSFName name) {
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
