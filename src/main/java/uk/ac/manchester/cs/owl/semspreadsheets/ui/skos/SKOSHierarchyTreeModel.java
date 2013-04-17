/*******************************************************************************
 * Copyright (c) 2009-2013, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.skos;

import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.skos.SKOSConcept;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.skos.SKOSHierarchyReader;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ClassHierarchyTreeModel;

public class SKOSHierarchyTreeModel extends ClassHierarchyTreeModel {
	
	public class SKOSTopNode extends DefaultMutableTreeNode {
		
		private static final long serialVersionUID = 4707804678459767953L;

		public SKOSTopNode() {
			super("Top");
		}
	}
	
	
	private static final Logger logger = Logger.getLogger(SKOSHierarchyTreeModel.class);
	private DefaultMutableTreeNode rootNode;
	private SKOSHierarchyReader skosReader;	
	
	public SKOSHierarchyTreeModel(OntologyManager ontologyManager,OWLOntology ontology) {
		super(ontologyManager,ontology);
		logger.debug("Using SKOSHierarchyTreeModel for "+ontology.getOntologyID().getOntologyIRI());				
	}	
	
	@Override
	public Object getRoot() {
		return rootNode;
	}
	
	@Override
	protected void buildTreeModel() {
		rootNode = new SKOSTopNode();
		
		skosReader = new SKOSHierarchyReader(getOntologyManager(), getOntology());		
		Set<SKOSConcept> topConcepts = skosReader.getTopConcepts();
		for (SKOSConcept concept : topConcepts) {			
			SKOSHierarchyTreeNode node = new SKOSHierarchyTreeNode(concept,getOntologyManager());			
			rootNode.add(node);
			storeIRIForNode(IRI.create(concept.getURI()), node);
			buildChildren(node);
		}		
	}		
	
	private void buildChildren(SKOSHierarchyTreeNode node) {
		SKOSConcept concept = node.getSKOSConcept();
		for (SKOSConcept c : skosReader.getNarrowerThan(concept)) {
			SKOSHierarchyTreeNode newNode = new SKOSHierarchyTreeNode(c,getOntologyManager());
			node.add(newNode);
			storeIRIForNode(IRI.create(c.getURI()), newNode);
			buildChildren(newNode);
		}
	}		
}
