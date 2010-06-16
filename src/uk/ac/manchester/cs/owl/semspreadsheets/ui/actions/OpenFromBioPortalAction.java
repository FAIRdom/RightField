package uk.ac.manchester.cs.owl.semspreadsheets.ui.actions;

import java.awt.event.ActionEvent;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class OpenFromBioPortalAction extends WorkbookFrameAction {

    public OpenFromBioPortalAction(WorkbookFrame workbookFrame) {
        super("Open from BioPortal...", workbookFrame);
    }

    public void actionPerformed(ActionEvent e) {
    	try {
            getWorkbookFrame().loadBioportalOntology();
        }
        catch (OWLOntologyCreationException e1) {
            ErrorHandler.getErrorHandler().handleError(e1);
        }
    }
}
