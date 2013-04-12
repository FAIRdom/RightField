/*******************************************************************************
 * Copyright (c) 2009-2013, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.model.skos;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;

public class SKOSDetector {
	
	//determines whether the ontology passed is a SKOS document
	public static boolean isSKOS(OWLOntology ontology) {
		return ontology.containsClassInSignature(IRI.create("http://www.w3.org/2004/02/skos/core#Concept"));
	}
	
	public static boolean isSKOSEntity(OWLEntity entity,OntologyManager ontologyManager) {
		return isSKOSEntity(entity.getIRI(), ontologyManager);
	}
	
	public static boolean isSKOSEntity(IRI entityIRI,OntologyManager ontologyManager) {
		for (OWLOntology ontology : ontologyManager.getOntologiesForEntityIRI(entityIRI)) {
			if (SKOSDetector.isSKOS(ontology)) {
				return true;
			}
		}
		return false;
	}
}
