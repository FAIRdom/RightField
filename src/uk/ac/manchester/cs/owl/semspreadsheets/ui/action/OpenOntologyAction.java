package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public class OpenOntologyAction extends WorkbookFrameAction {

    public OpenOntologyAction(WorkbookFrame workbookFrame) {
        super("Open ontology...", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_O, true, false);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            getWorkbookFrame().loadOntology();
        }
        catch (OWLOntologyCreationException e1) {
            ErrorHandler.getErrorHandler().handleError(e1);
        }
    }
}
