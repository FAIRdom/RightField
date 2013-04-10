package uk.ac.manchester.cs.owl.semspreadsheets.ui.skos;

import java.net.URI;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSDataset;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.skos.SKOSHierarchyReader;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ClassHierarchyTreeModel;

public class SKOSHierarchyTreeModel extends ClassHierarchyTreeModel {
	
	private static final Logger logger = Logger.getLogger(SKOSHierarchyTreeModel.class);
	private DefaultMutableTreeNode rootNode;
	private SKOSHierarchyReader skosReader;
	
	public SKOSHierarchyTreeModel(OntologyManager ontologyManager,OWLOntology ontology) {
		super(ontologyManager,ontology);
		logger.debug("Using SKOSHierarchyTreeModel for "+ontology.getOntologyID().getOntologyIRI());
		
		
	}

	@Override
	protected void buildTreeModel() {
		rootNode = new DefaultMutableTreeNode("Top");
		skosReader = new SKOSHierarchyReader(getOntologyManager(), getOntology());
		SKOSDataset dataset = skosReader.getDataset(getOntology());
		Set<SKOSConcept> topConcepts = skosReader.getTopConcepts();
		for (SKOSConcept concept : topConcepts) {
			SKOSHierarchyTreeNode node = new SKOSHierarchyTreeNode(concept,dataset);
			rootNode.add(node);
			buildChildren(node,dataset);
		}
		
	}		

	@Override
	public Object getRoot() {
		return rootNode;
	}
	
	private void buildChildren(SKOSHierarchyTreeNode node,SKOSDataset dataset) {
		SKOSConcept concept = node.getSKOSConcept();
		for (SKOSConcept c : skosReader.getNarrowerThan(concept)) {
			SKOSHierarchyTreeNode newNode = new SKOSHierarchyTreeNode(c,dataset);
			node.add(newNode);
			buildChildren(newNode,dataset);
		}
	}	
}

class SKOSHierarchyTreeNode extends DefaultMutableTreeNode {
		
	private static final long serialVersionUID = 768120710476700086L;
	private final SKOSDataset dataset;	
	
	public SKOSHierarchyTreeNode(SKOSConcept concept,SKOSDataset dataset) {
		super(concept);
		this.dataset = dataset;
	}
	
	public SKOSConcept getSKOSConcept() {
		return (SKOSConcept)getUserObject();
	}	
	
	public String getLabelText() {
		String result = null;
		result = getPrefLabel();
		if (result==null) {
			result=getSKOSConcept().getURI().getRawFragment();
		}
		return result;
	}
	
	private String getPrefLabel() {
		Set<SKOSAnnotation> skosAnnotations = getSKOSConcept().getSKOSAnnotationsByURI(dataset, URI.create("http://www.w3.org/2004/02/skos/core#prefLabel"));
		if (skosAnnotations.size()>0) {
			SKOSAnnotation annotation = skosAnnotations.iterator().next();
			return annotation.getAnnotationValueAsConstant().getLiteral();
		}
		else {
			return null;
		}		
	}
}
