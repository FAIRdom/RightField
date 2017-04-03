/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class OpenOntologyAction extends WorkbookFrameAction {

    public OpenOntologyAction(WorkbookFrame workbookFrame) {
        super("Open ontology...", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_O, true, false);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            getWorkbookFrame().loadOntology();
        }
        catch (Exception e1) {        	
            //will already have been handled inside LoadOntologyTask
        }
    }
}
