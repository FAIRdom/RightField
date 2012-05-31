/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.change;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 01-Nov-2009
 */
public abstract class WorkbookChange {

    private Workbook workbook;

    public WorkbookChange(Workbook workbook) {
        this.workbook = workbook;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public abstract void accept(WorkbookChangeVisitor visitor);

    public abstract boolean isUndoable();
}
