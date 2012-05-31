/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.change;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 01-Nov-2009
 * 
 * Author: Stuart Owen
 * Date: 30-Sep-2010
 */
public class SetCellValue extends SpreadsheetChange {

    private Object oldValue;

    private Object newValue;
    
    /**
     * Constructs the change with the assumption the workbook is the same as the one that belongs to the sheet
     * @param sheet
     * @param col
     * @param row
     * @param oldValue
     * @param newValue
     */
    public SetCellValue(Sheet sheet,int col,int row,Object oldValue,Object newValue) {
    	this(sheet.getWorkbook(),sheet,col,row,oldValue,newValue);
    }

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
