/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Listener for events related to the {@link OntologyManager}
 * @author Stuart Owen
 *
 */
public interface OntologyManagerListener {
	
	/**
	 * An ontology has been loaded or removed.
	 */
	void ontologiesChanged();
	
	/**
	 * An ontology has been selected within the UI
	 * 
	 * @param ontology
	 */
	void ontologySelected(OWLOntology ontology);
	//FIXME: ontologySelected may be more appropriatly tracked in the EntitySelectionModel and its listener. 
}
