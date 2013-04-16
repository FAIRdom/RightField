package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public interface HierarchyTree {
	public boolean containsEntity(OWLEntity entity);
	public void setSelectedEntity(OWLEntity entity);
	public OWLEntity getSelectedEntity();
	public OWLOntology getOntology();
	public void previewSelectedClass();
	public void clearSelection();		
}
