package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
public class FormatBoldAction extends SelectedCellsAction {

    public FormatBoldAction(WorkbookFrame workbookFrame) {
        super("Cells bold", workbookFrame);
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
