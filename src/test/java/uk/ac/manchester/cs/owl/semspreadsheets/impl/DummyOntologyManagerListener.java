package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManagerListener;

public class DummyOntologyManagerListener implements OntologyManagerListener {
	
	public boolean ontologiesChanedFired = false;
	public OWLOntology ontologySelected = null;
	
	public DummyOntologyManagerListener() {
		reset();
	}	
	
	public void reset() {
		ontologiesChanedFired = false;
	}
	
	public boolean isOntologiesChanedFired() {
		return ontologiesChanedFired;
	}
	
	public OWLOntology getOntologySelected() {
		return ontologySelected;
	}
	
	@Override
	public void ontologiesChanged() {
		ontologiesChanedFired=true;
	}

	@Override
	public void ontologySelected(OWLOntology ontology) {
		ontologySelected=ontology;
		
	}

}
