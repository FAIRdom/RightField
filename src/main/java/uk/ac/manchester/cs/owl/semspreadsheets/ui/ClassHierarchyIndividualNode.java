/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import javax.swing.tree.DefaultMutableTreeNode;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * 
 */
@SuppressWarnings("serial")
public class ClassHierarchyIndividualNode extends DefaultMutableTreeNode {

    public ClassHierarchyIndividualNode(OWLNamedIndividual individual) {
        super(individual);
    }
}
