/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
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
