/*******************************************************************************
 * Copyright (c) 2009-2013, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import java.net.URI;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;

/**
 * Task to handle opening an ontology from a URL
 * 
 * @author Stuart Owen
 *
 */
public class LoadOntologyFromURITask extends AbstractTask<OWLOntology, OWLOntologyCreationException> {
	
	private final URI uri;

	public LoadOntologyFromURITask(URI uri) {
		this.uri = uri;		     
    }

    public OWLOntology runTask() throws OWLOntologyCreationException {
    	try {    		
    		return getOntologyManager().loadOntology(IRI.create(uri));
    	}
    	catch(OWLOntologyCreationException e) {    		
    		ErrorHandler.getErrorHandler().handleError(e,IRI.create(uri));
    		throw e;
    	}
    }

    public String getTitle() {
        return "Loading ontology";
    }

}
