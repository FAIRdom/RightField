/*******************************************************************************
 * Copyright (c) 2009-2013, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.skos;

import javax.swing.tree.TreeModel;

import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ClassHierarchyTree;

@SuppressWarnings("serial")
public class SKOSHierarchyTree extends ClassHierarchyTree {
	
	public SKOSHierarchyTree(final WorkbookManager manager, OWLOntology ontology) {
		super(manager,ontology);
	}
	
	@Override
	protected void setupCellRenderer() {
    	setCellRenderer(new SKOSTreeCellRenderer());
    }
    
	@Override
    protected TreeModel createTreeModel(OntologyManager ontologyManager, OWLOntology ontology) {
    	return new SKOSHierarchyTreeModel(ontologyManager, ontology);
    }

}
