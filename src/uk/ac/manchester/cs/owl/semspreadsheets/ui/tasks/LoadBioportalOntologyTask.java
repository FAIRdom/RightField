package uk.ac.manchester.cs.owl.semspreadsheets.ui.tasks;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class LoadBioportalOntologyTask extends AbstractTask<OWLOntology, OWLOntologyCreationException> {

	private IRI iri;

    public LoadBioportalOntologyTask(IRI iri) {
        this.iri = iri;
    }


    public OWLOntology runTask() throws OWLOntologyCreationException {
        return getWorkbookFrame().getWorkbookManager().loadOntology(iri);
    }

    public String getTitle() {
        return "Loading bioportal ontology";
    }

    public void cancelTask() {
    }
}
