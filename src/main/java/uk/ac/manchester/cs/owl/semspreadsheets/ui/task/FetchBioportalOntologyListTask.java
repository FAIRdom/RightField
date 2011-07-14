package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import java.util.Collection;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryManager;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepositoryAccessor;

public class FetchBioportalOntologyListTask extends AbstractTask<Collection<RepositoryItem>, Exception> {

	private static Logger logger = Logger.getLogger(FetchBioportalOntologyListTask.class);
	
	public void cancelTask() {
		logger.info("Cancel FetchBioportalOntologyListTask requested but ignored");
	}

	public String getTitle() {
		return "Fetching BioPortal ontology list";
	}

	public Collection<RepositoryItem> runTask() throws Exception {
		BioPortalRepositoryAccessor bioPortalRepositoryAccessor = RepositoryManager
		.getInstance().getBioPortalRepositoryAccessor();
		return bioPortalRepositoryAccessor.getRepository().getOntologies();
	}

}
