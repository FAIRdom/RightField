package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;

public class OntologyManagerTest {
	
	private OntologyManager ontologyManager;

	@Before
	public void createOntologyManager() {
		ontologyManager = new WorkbookManager().getOntologyManager();		
	}

	@Test
	public void loadJERMOntology() throws Exception {
		Set<OWLOntology> loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(0,loadedOntologies.size());
		
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		assertNotNull(ontology);
		assertNotNull(ontology.getOntologyID());		
		assertEquals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology"),ontology.getOntologyID().getOntologyIRI());
		
		loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(1,loadedOntologies.size());
		ontology = loadedOntologies.iterator().next();
		assertNotNull(ontology);
		assertNotNull(ontology.getOntologyID());
		assertEquals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology"),ontology.getOntologyID().getOntologyIRI());
	}
	
	@Test
	public void loadMGEDOntology() throws Exception {
		//to cover a problem with the MGED ontology loading an ontology with a nil IRI.
		//a short term fix for this, is to remove ontologies that have nil IRI's.
		//
		//the Protege ontology, that is imported, should also be stripped out
		Set<OWLOntology> loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(0,loadedOntologies.size());
		
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.mgedOntologyURI());
		assertNotNull(ontology);
		assertNotNull(ontology.getOntologyID());		
		assertEquals(IRI.create("http://mged.sourceforge.net/ontologies/MGEDOntology.owl"),ontology.getOntologyID().getOntologyIRI());
		
		loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(1,loadedOntologies.size());
		ontology = loadedOntologies.iterator().next();
		assertNotNull(ontology);
		assertNotNull(ontology.getOntologyID());		
		assertEquals(IRI.create("http://mged.sourceforge.net/ontologies/MGEDOntology.owl"),ontology.getOntologyID().getOntologyIRI());
		
		for (OWLOntology o : ontologyManager.getLoadedOntologies()) {
			assertNotNull(o.getOntologyID());
			assertNotNull(o.getOntologyID().getOntologyIRI());
		}
	}

}
