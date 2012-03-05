package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
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
