package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import javax.swing.AbstractAction;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 20-Sep-2009
 */
public abstract class SpreadSheetAction extends AbstractAction {

    private WorkbookManager workbookManager;

    private WorkbookFrame workbookFrame;

    public SpreadSheetAction(String name, WorkbookManager workbookManager, WorkbookFrame workbookFrame) {
        super(name);
        this.workbookManager = workbookManager;
        this.workbookFrame = workbookFrame;
    }

    public WorkbookManager getSpreadSheetManager() {
        return workbookManager;
    }

    public WorkbookFrame getSpreadSheetFrame() {
        return workbookFrame;
    }
}
