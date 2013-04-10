/*******************************************************************************
 * Copyright (c) 2009-2013, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.skos;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.Icons;

public class SKOSTreeCellRenderer implements TreeCellRenderer {
	
	private DefaultTreeCellRenderer treeCellRendererDelegate = new DefaultTreeCellRenderer();
	
	public SKOSTreeCellRenderer() {
		
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		JLabel label = (JLabel) treeCellRendererDelegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		if (value instanceof SKOSHierarchyTreeNode) {
			SKOSHierarchyTreeNode node = (SKOSHierarchyTreeNode)value;
			label.setText(node.getLabelText());
			label.setIcon(Icons.getSKOSConceptIcon());
		}
					
		return label;
	}		
 
}
