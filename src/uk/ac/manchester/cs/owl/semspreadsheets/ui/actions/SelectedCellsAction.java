package uk.ac.manchester.cs.owl.semspreadsheets.ui.actions;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.CellSelectionListener;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
public abstract class SelectedCellsAction extends WorkbookFrameAction implements CellSelectionListener{

    protected SelectedCellsAction(String name, WorkbookFrame workbookFrame) {
        super(name, workbookFrame);
        workbookFrame.getWorkbookManager().getSelectionModel().addCellSelectionListener(this);
    }

    public void selectionChanged(Range range) {
        setEnabled(range.isCellSelection());
    }

    public Range getSelectedRange() {
        return getWorkbookFrame().getWorkbookManager().getSelectionModel().getSelectedRange();
    }
}
