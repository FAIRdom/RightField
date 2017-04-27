/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.skos;

import javax.swing.tree.DefaultMutableTreeNode;

import org.semanticweb.skos.SKOSConcept;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;

public class SKOSHierarchyTreeNode extends DefaultMutableTreeNode {
		
	private static final long serialVersionUID = 768120710476700086L;	
	private final OntologyManager ontologyManager;	
	
	public SKOSHierarchyTreeNode(SKOSConcept concept,OntologyManager ontologyManager) {
		super(concept);
		
		//FIXME: the reference to the dataset and ontology manager are purely to get the rendered label text.		
		this.ontologyManager = ontologyManager;
	}
	
	public SKOSConcept getSKOSConcept() {
		return (SKOSConcept)getUserObject();
	}	
	
	public String getLabelText() {
		return ontologyManager.getRendering(getSKOSConcept());
	}		
}
