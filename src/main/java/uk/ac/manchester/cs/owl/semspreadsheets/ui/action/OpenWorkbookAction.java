/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Action to open a a spreadsheet from disk
 * @author Stuart Owen
 * @author Matthew Horridge
 *
 */
@SuppressWarnings("serial")
public class OpenWorkbookAction extends WorkbookFrameAction {

    public OpenWorkbookAction(WorkbookFrame workbookFrame) {
        super("Open spreadsheet...", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_O);
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        getWorkbookFrame().openWorkbook();        
    }
}
