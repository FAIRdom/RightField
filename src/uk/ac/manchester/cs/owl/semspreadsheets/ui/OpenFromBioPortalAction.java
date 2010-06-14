package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.event.ActionEvent;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryManager;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepositoryAccessor;

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
        BioPortalRepositoryAccessor bioPortalRepositoryAccessor = RepositoryManager.getInstance().getBioPortalRepositoryAccessor();
        if (!bioPortalRepositoryAccessor.getRepository().getOntologies().isEmpty()) {
            RepositoryItem item =RepositoryPanel.showDialog(getWorkbookFrame(), bioPortalRepositoryAccessor);
            if(item != null) {
                try {
                    getWorkbookFrame().getWorkbookManager().loadOntology(item.getPhysicalIRI());
                }
                catch (OWLOntologyCreationException e1) {
                    ErrorHandler.getErrorHandler().handleError(e1);
                }
            }
        }
    }
}
