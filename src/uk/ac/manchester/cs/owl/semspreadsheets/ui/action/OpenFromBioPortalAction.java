package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 * 
 * Author: Stuart Owen
 * Date: 15-June-2010
 * 
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class OpenFromBioPortalAction extends WorkbookFrameAction {		

    public OpenFromBioPortalAction(WorkbookFrame workbookFrame) {
        super("Open from BioPortal...", workbookFrame);
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
