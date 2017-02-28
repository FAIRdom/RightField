/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action to open a a spreadsheet from disk
 * @author Stuart Owen
 * @author Matthew Horridge
 *
 */
@SuppressWarnings("serial")
public class AddLinkCellToRowAction extends WorkbookFrameAction {

    public AddLinkCellToRowAction(WorkbookManager workbookManager, WorkbookFrame workbookFrame) {
        super("Link Row to row PLM", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_K);
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        getWorkbookManager().addLinkCells(false, true);
    }
}
