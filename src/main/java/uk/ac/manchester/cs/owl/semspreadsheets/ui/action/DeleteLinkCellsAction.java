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
public class DeleteLinkCellsAction extends WorkbookFrameAction {

    public DeleteLinkCellsAction(WorkbookManager workbookManager, WorkbookFrame workbookFrame) {
        super("Delete link", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_J, true, false);

    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        getWorkbookManager().addLink(true, true);
    }
}
