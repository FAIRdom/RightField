package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.OntologyManagerListener;

public class DummyOntologyManagerListener implements OntologyManagerListener {
	
	public boolean ontologiesChangedFired = false;
	public OWLOntology ontologySelected = null;
	
	public DummyOntologyManagerListener() {
		reset();
	}	
	
	public void reset() {
		ontologiesChangedFired = false;
	}
	
	public boolean isOntologiesChangedFired() {
		return ontologiesChangedFired;
	}
	
	public OWLOntology getOntologySelected() {
		return ontologySelected;
	}
	
	@Override
	public void ontologiesChanged() {
		ontologiesChangedFired=true;
	}

	@Override
	public void ontologySelected(OWLOntology ontology) {
		ontologySelected=ontology;
		
	}

}
