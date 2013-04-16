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

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class ClassHierarchyTreeModelTest {
		
	private OntologyManager ontologyManager;
	private OWLOntology ontology;
	private ClassHierarchyTreeModel treeModel;

	@Before
	public void setup() throws Exception {
		WorkbookManager workbookManager = new WorkbookManager();
		ontologyManager = workbookManager.getOntologyManager();
		ontology = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		treeModel = new ClassHierarchyTreeModel(ontologyManager, ontology);
	}
	
	@Test
	public void testGetNodesForIRI() {
		IRI iri = IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#fffff");
		Collection<DefaultMutableTreeNode> nodes = treeModel.getNodesForIRI(iri);
		assertTrue(nodes.isEmpty());
		
		iri = IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#Data");
		nodes = treeModel.getNodesForIRI(iri);		
		List<DefaultMutableTreeNode> asList = Arrays.asList(nodes.toArray(new DefaultMutableTreeNode [] {}));
		assertEquals(1,asList.size());
		assertTrue(asList.get(0) instanceof ClassHierarchyTreeNode);
		assertEquals(iri,((ClassHierarchyTreeNode)asList.get(0)).getOWLClasses().iterator().next().getIRI());		
	}
	
	@Test
	public void testGetTreePathsForEntity() throws Exception {
		IRI iri = IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#fffff");
		OWLEntity entity = new OWLClassImpl(iri);
		Collection<TreePath> paths = treeModel.getTreePathsForEntity(entity);
		assertTrue(paths.isEmpty());
		
		iri = IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#Data");
		entity = new OWLClassImpl(iri);
		paths = treeModel.getTreePathsForEntity(entity);
		assertFalse(paths.isEmpty());	
	}

}
