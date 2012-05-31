/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
@SuppressWarnings("serial")
public class FormatBoldAction extends SelectedCellsAction {

    public FormatBoldAction(WorkbookManager workbookManager) {
        super("Cells bold", workbookManager);
        setAcceleratorKey(KeyEvent.VK_B);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        Range range = getSelectedRange();
        if (range.isCellSelection()) {
            for(int col = range.getFromColumn(); col < range.getToColumn() + 1; col++) {
                for(int row = range.getFromRow(); row < range.getToRow() + 1; row++) {
                    Cell cell = range.getSheet().getCellAt(col, row);
                    if (cell != null) {
                        cell.setBold(true);
                    }
                }
            }
        }
    }
}
