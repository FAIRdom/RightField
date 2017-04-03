/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.skos;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skosapibinding.SKOStoOWLConverter;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ClassHierarchyTree;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.HierarchyTreeModel;

@SuppressWarnings("serial")
public class SKOSHierarchyTree extends ClassHierarchyTree {
	private static final Logger logger = Logger.getLogger(SKOSHierarchyTree.class);
	
	public SKOSHierarchyTree(final WorkbookManager manager, OWLOntology ontology) {
		super(manager,ontology);		
	}
	
	@Override
	protected void setupCellRenderer() {
    	setCellRenderer(new SKOSTreeCellRenderer());
    }
    
	@Override
    protected HierarchyTreeModel createTreeModel(OntologyManager ontologyManager, OWLOntology ontology) {
    	return new SKOSHierarchyTreeModel(ontologyManager, ontology);
    }

	@Override
	public OWLEntity getSelectedEntity() {
		TreePath[] selectedPaths = getSelectionPaths();
		OWLEntity entity = null;
		if (selectedPaths == null) {
			return null;
		}
		for (TreePath path : selectedPaths) {
			Object node = path.getLastPathComponent();
			if (node instanceof SKOSHierarchyTreeNode) {
				SKOSConcept concept = ((SKOSHierarchyTreeNode)node).getSKOSConcept();
				logger.debug("Selected SKOS concept: "+concept.getURI().toString());
				entity = new SKOStoOWLConverter().getAsOWLIndiviudal(concept);
				break;
			}
		}
		return entity;
	}
}
