package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
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
