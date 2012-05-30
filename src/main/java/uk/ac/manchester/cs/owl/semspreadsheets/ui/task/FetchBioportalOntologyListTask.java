/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import java.util.Collection;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryManager;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepositoryAccessor;

public class FetchBioportalOntologyListTask extends AbstractTask<Collection<RepositoryItem>, Exception> {	

	public String getTitle() {
		return "Fetching BioPortal ontology list";
	}

	public Collection<RepositoryItem> runTask() throws Exception {
		BioPortalRepositoryAccessor bioPortalRepositoryAccessor = RepositoryManager
		.getInstance().getBioPortalRepositoryAccessor();
		return bioPortalRepositoryAccessor.getRepository().getOntologies();
	}

}
