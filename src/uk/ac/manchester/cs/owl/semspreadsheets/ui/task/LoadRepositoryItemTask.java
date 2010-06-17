package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;

public class LoadRepositoryItemTask extends AbstractTask<OWLOntology, OWLOntologyCreationException> {

	private RepositoryItem repositoryItem;
	
	private static Logger logger = Logger.getLogger(LoadRepositoryItemTask.class);

    public LoadRepositoryItemTask(RepositoryItem repositoryItem) {
        this.repositoryItem = repositoryItem;
    }

    public OWLOntology runTask() throws OWLOntologyCreationException {
        return getWorkbookFrame().getWorkbookManager().loadOntology(repositoryItem.getPhysicalIRI());
    }

    public String getTitle() {    	
        return "Fetching '" + repositoryItem.getHumanReadableName() +"' ontology";
    }

    public void cancelTask() {
    	logger.info("Cancel LoadRepositoryItemTask requested but ignored");
    }
}
