package uk.ac.manchester.cs.owl.semspreadsheets.change;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 01-Nov-2009
 */
public class SetCellValue extends SpreadsheetChange {

    private Object oldValue;

    private Object newValue;

    public SetCellValue(Workbook workbook, Sheet sheet, int col, int row, Object oldValue, Object newValue) {
        super(workbook, sheet, col, row);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public boolean isUndoable() {
        return true;
    }

    public void accept(WorkbookChangeVisitor visitor) {
        visitor.visit(this);
    }
}
