/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import javax.swing.JTree;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public abstract class HierarchyTree extends JTree {
		
	private static final long serialVersionUID = 4664441252592179022L;
	
	public abstract boolean containsEntity(OWLEntity entity);
	public abstract void setSelectedEntity(OWLEntity entity);
	public abstract OWLEntity getSelectedEntity();
	public abstract OWLOntology getOntology();
	public abstract void previewSelectedClass();		
}
