/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.CellSelectionListener;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
@SuppressWarnings("serial")
public abstract class SelectedCellsAction extends WorkbookAction implements CellSelectionListener{

    protected SelectedCellsAction(String name, WorkbookManager workbookManager) {
        super(name, workbookManager);
        workbookManager.getSelectionModel().addCellSelectionListener(this);
    }

    public void selectionChanged(Range range) {
        setEnabled(range.isCellSelection());
    }

    public Range getSelectedRange() {
        return getWorkbookManager().getSelectionModel().getSelectedRange();
    }
}
