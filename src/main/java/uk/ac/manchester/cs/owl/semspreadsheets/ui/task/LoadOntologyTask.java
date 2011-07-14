package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import java.io.File;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 03-Feb-2010
 */
public class LoadOntologyTask extends AbstractTask<OWLOntology, OWLOntologyCreationException> {

    private File file;

    public LoadOntologyTask(File file) {
        this.file = file;
    }

    public OWLOntology runTask() throws OWLOntologyCreationException {
    	try {
    		return getWorkbookFrame().getWorkbookManager().loadOntology(IRI.create(file));
    	}
    	catch(OWLOntologyCreationException e) {    		
    		ErrorHandler.getErrorHandler().handleError(e);
    		throw e;
    	}
    }

    public String getTitle() {
        return "Loading ontology";
    }

    public void cancelTask() {
    	setCancelled(true);
    }
}
