package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import java.io.File;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
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
}
