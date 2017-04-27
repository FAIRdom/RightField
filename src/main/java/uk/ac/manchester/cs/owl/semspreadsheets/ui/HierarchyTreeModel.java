/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.Collection;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.semanticweb.owlapi.model.OWLEntity;

public interface HierarchyTreeModel extends TreeModel {
	public  Collection<TreePath> getTreePathsForEntity(OWLEntity entity);
}
