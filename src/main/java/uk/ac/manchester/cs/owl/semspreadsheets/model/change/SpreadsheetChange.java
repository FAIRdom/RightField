/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.change;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;


/**
 * @author Matthew Horridge 
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
