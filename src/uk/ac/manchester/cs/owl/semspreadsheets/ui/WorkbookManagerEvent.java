package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
public class WorkbookManagerEvent {

    private WorkbookManager manager;

    public WorkbookManagerEvent(WorkbookManager manager) {
        this.manager = manager;
    }

    public WorkbookManager getSource() {
        return manager;
    }
}
