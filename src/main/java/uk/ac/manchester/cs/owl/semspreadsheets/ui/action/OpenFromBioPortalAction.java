/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class OpenFromBioPortalAction extends WorkbookFrameAction {		

    public OpenFromBioPortalAction(WorkbookFrame workbookFrame) {
        super("Open ontology from BioPortal...", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_B,true,false);
    }

    public void actionPerformed(ActionEvent e) {
    	try {
            getWorkbookFrame().loadBioportalOntology();
        }
        catch (Exception e1) {
            ErrorHandler.getErrorHandler().handleError(e1);
        }        
    }
}
