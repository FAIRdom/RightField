/*******************************************************************************
 * Copyright (c) 2009-2013, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;

/**
 * 
 * @author Stuart Owen
 *
 */
public class LoadRepositoryItemTask extends AbstractTask<OWLOntology, OWLOntologyCreationException> {

	private RepositoryItem repositoryItem;	

    public LoadRepositoryItemTask(RepositoryItem repositoryItem) {
        this.repositoryItem = repositoryItem;
    }

    public OWLOntology runTask() throws OWLOntologyCreationException {
        return getOntologyManager().loadOntology(repositoryItem.getPhysicalIRI());
    }

    public String getTitle() {    	
        return "Fetching '" + repositoryItem.getHumanReadableName() +"' ontology";
    }    

}
