/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class OntologyCellRenderer implements TreeCellRenderer, ListCellRenderer {

    private DefaultListCellRenderer listCellRendererDelegate = new DefaultListCellRenderer();

    private DefaultTreeCellRenderer treeCellRendererDelegate = new DefaultTreeCellRenderer();

	private final OntologyManager ontologyManager;

    public OntologyCellRenderer(OntologyManager ontologyManager) {
        this.ontologyManager = ontologyManager;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) treeCellRendererDelegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if(value instanceof ClassHierarchyTreeNode) {
            ClassHierarchyTreeNode node = (ClassHierarchyTreeNode) value;
            setupRenderer(label, node.getOWLClasses().iterator().next());
        }
        else if(value instanceof ClassHierarchyIndividualNode) {
            ClassHierarchyIndividualNode node = (ClassHierarchyIndividualNode) value;
            setupRenderer(label, (OWLNamedIndividual) node.getUserObject());
        }
        return label;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) listCellRendererDelegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setupRenderer(label, value);
        return label;
    }

    private void setupRenderer(JLabel label, Object value) {
        if(value instanceof OWLEntity) {
        	OWLEntity entity = (OWLEntity)value;
            label.setIcon(Icons.getOWLEntityIcon(entity));
            label.setText(ontologyManager.getRendering(entity));                                  
        }        
        else {
            label.setIcon(null);
        }
    }
}
