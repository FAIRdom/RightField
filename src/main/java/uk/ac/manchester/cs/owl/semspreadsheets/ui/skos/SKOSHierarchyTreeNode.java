/*******************************************************************************
 * Copyright (c) 2009-2013, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.skos;

import java.net.URI;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSDataset;

class SKOSHierarchyTreeNode extends DefaultMutableTreeNode {
		
	private static final long serialVersionUID = 768120710476700086L;
	private final SKOSDataset dataset;	
	
	public SKOSHierarchyTreeNode(SKOSConcept concept,SKOSDataset dataset) {
		super(concept);
		this.dataset = dataset;
	}
	
	public SKOSConcept getSKOSConcept() {
		return (SKOSConcept)getUserObject();
	}	
	
	public String getLabelText() {
		String result = null;
		result = getPrefLabel();
		if (result==null) {
			result=getSKOSConcept().getURI().getRawFragment();
		}
		return result;
	}
	
	private String getPrefLabel() {
		Set<SKOSAnnotation> skosAnnotations = getSKOSConcept().getSKOSAnnotationsByURI(dataset, URI.create("http://www.w3.org/2004/02/skos/core#prefLabel"));
		if (skosAnnotations.size()>0) {
			SKOSAnnotation annotation = skosAnnotations.iterator().next();
			return annotation.getAnnotationValueAsConstant().getLiteral();
		}
		else {
			return null;
		}		
	}
}
