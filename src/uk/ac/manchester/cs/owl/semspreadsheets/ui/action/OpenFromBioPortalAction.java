package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

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
	
	private static Logger logger = Logger.getLogger(OpenFromBioPortalAction.class);

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
        catch (Exception e2) {
        	//FIXME: need to report non OWLOntology errors back to the user.
        	logger.error("Exception fetching BioportalOntologies",e2);
        }
    }
}
