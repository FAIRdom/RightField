package uk.ac.manchester.cs.owl.semspreadsheets.change;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 01-Nov-2009
 */
public abstract class SpreadsheetChange extends WorkbookChange {

    private Sheet sheet;

    private int col;

    private int row;

    protected SpreadsheetChange(Workbook workbook, Sheet sheet, int col, int row) {
        super(workbook);
        this.sheet = sheet;
        this.col = col;
        this.row = row;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
