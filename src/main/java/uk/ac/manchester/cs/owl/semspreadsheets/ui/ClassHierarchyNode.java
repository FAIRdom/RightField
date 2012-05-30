/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.NodeFactory;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class ClassHierarchyNode extends DefaultMutableTreeNode {    

    public ClassHierarchyNode() {
        super(NodeFactory.getOWLClassTopNode());        
    }

    public ClassHierarchyNode(Node<OWLClass> clses) {
        super(clses);        
    }

    @SuppressWarnings("unchecked")
	public Set<OWLClass> getOWLClasses() {
        return ((Node<OWLClass>) getUserObject()).getEntities();
    }
}
