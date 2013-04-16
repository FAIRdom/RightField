package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.Collection;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.semanticweb.owlapi.model.OWLEntity;

public interface HierarchyTreeModel extends TreeModel {
	public  Collection<TreePath> getTreePathsForEntity(OWLEntity entity);
}
