package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class ClassHierarchyTreeTest {

	private OntologyManager ontologyManager;
	private OWLOntology ontology;
	private HierarchyTree tree;

	@Before
	public void setup() throws Exception {
		WorkbookManager workbookManager = new WorkbookManager();
		ontologyManager = workbookManager.getOntologyManager();
		ontology = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		tree = new ClassHierarchyTree(workbookManager, ontology);
	}
	
	@Test
	public void testContainsEntity() throws Exception {
		OWLEntity e = new OWLClassImpl(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#Data"));
		assertTrue(tree.containsEntity(e));
		e = new OWLClassImpl(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#fffff"));
		assertFalse(tree.containsEntity(e));
	}
	
	@Test
	public void testGetOntology() {
		assertEquals(ontology,tree.getOntology());
	}
}
