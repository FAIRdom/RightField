package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import javax.swing.tree.DefaultMutableTreeNode;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 10-Nov-2009
 */
@SuppressWarnings("serial")
public class ClassHierarchyIndividualNode extends DefaultMutableTreeNode {

    public ClassHierarchyIndividualNode(OWLNamedIndividual individual) {
        super(individual);
    }
}
