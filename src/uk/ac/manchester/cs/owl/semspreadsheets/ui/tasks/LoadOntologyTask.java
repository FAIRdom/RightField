package uk.ac.manchester.cs.owl.semspreadsheets.ui.tasks;

import java.io.File;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

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
        return getWorkbookFrame().getWorkbookManager().loadOntology(IRI.create(file));
    }

    public String getTitle() {
        return "Loading ontology";
    }

    public void cancelTask() {
    }
}
