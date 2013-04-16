package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.skos.SKOSHierarchyTreeModel;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.skos.SKOSHierarchyTreeNode;

public class SKOSHierarchyTreeModelTest {
	private OntologyManager ontologyManager;
	private OWLOntology ontology;
	private SKOSHierarchyTreeModel treeModel;

	@Before
	public void setup() throws Exception {
		WorkbookManager workbookManager = new WorkbookManager();
		ontologyManager = workbookManager.getOntologyManager();
		ontology = ontologyManager.loadOntology(DocumentsCatalogue.exampleSKOSURI());
		treeModel = new SKOSHierarchyTreeModel(ontologyManager, ontology);
	}
	
	@Test
	public void testGetNodesForIRI() {
		IRI iri = IRI.create("http://www.fluffyboards.com/vocabulary#fff");
		Collection<DefaultMutableTreeNode> nodes = treeModel.getNodesForIRI(iri);
		assertTrue(nodes.isEmpty());
		
		iri = IRI.create("http://www.fluffyboards.com/vocabulary#product");
		nodes = treeModel.getNodesForIRI(iri);		
		List<DefaultMutableTreeNode> asList = Arrays.asList(nodes.toArray(new DefaultMutableTreeNode [] {}));
		assertEquals(1,asList.size());
		assertTrue(asList.get(0) instanceof SKOSHierarchyTreeNode);
		assertEquals(iri.toURI(),((SKOSHierarchyTreeNode)asList.get(0)).getSKOSConcept().getURI());
	}
	
	@Test
	public void testGetTreePathsForEntity() throws Exception {
		IRI iri = IRI.create("http://www.fluffyboards.com/vocabulary#fff");
		OWLEntity entity = new OWLNamedIndividualImpl(iri);
		Collection<TreePath> paths = treeModel.getTreePathsForEntity(entity);
		assertTrue(paths.isEmpty());
		
		iri = IRI.create("http://www.fluffyboards.com/vocabulary#product");
		entity = new OWLNamedIndividualImpl(iri);
		paths = treeModel.getTreePathsForEntity(entity);
		assertFalse(paths.isEmpty());	
	}
}
