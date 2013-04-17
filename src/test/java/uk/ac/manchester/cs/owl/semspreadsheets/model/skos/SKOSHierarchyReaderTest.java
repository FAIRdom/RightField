package uk.ac.manchester.cs.owl.semspreadsheets.model.skos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.skos.SKOSConcept;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class SKOSHierarchyReaderTest {
	
	private WorkbookManager workbookManager;
	private OntologyManager ontologyManager;

	@Before
	public void setupManagers() throws Exception {
		workbookManager = new WorkbookManager();
		ontologyManager = workbookManager.getOntologyManager();
	}
	
	
	
	@Test
	public void testGetTopConcepts() throws Exception {
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.exampleSKOSURI());
		SKOSHierarchyReader reader = new SKOSHierarchyReader(ontologyManager,
				ontology);
		Set<SKOSConcept> concepts = reader.getTopConcepts();
		assertEquals(3, concepts.size());
		URI[] expected = new URI[] {
				URI.create("http://www.fluffyboards.com/vocabulary#product"),
				URI.create("http://www.fluffyboards.com/vocabulary#review"),
				URI.create("http://www.fluffyboards.com/vocabulary#customer") };
		for (URI exp : expected) {
			boolean found = false;
			for (SKOSConcept concept : concepts) {
				if (concept.getURI().equals(exp)) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}
	
	@Test
	public void testGetTopConceptsCAST() throws Exception {
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.castSKOSURI());
		SKOSHierarchyReader reader = new SKOSHierarchyReader(ontologyManager,
				ontology);
		Set<SKOSConcept> concepts = reader.getTopConcepts();
		assertEquals(6, concepts.size());
		URI[] expected = new URI[] {
				URI.create("http://onto.nerc.ac.uk/CAST/1"),
				URI.create("http://onto.nerc.ac.uk/CAST/2"),
				URI.create("http://onto.nerc.ac.uk/CAST/3"),
				URI.create("http://onto.nerc.ac.uk/CAST/4"),
				URI.create("http://onto.nerc.ac.uk/CAST/5"),
				URI.create("http://onto.nerc.ac.uk/CAST/6")						
		};
		for (URI exp : expected) {
			boolean found = false;
			for (SKOSConcept concept : concepts) {				
				if (concept.getURI().equals(exp)) {
					found = true;
					break;
				}
			}
			assertTrue("Didn't find "+exp,found);
		}
	}
	
	@Test
	public void testGetSKOSConcept() throws Exception {
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.exampleSKOSURI());		
		SKOSHierarchyReader reader = new SKOSHierarchyReader(ontologyManager, ontology);		
		SKOSConcept concept = reader.getSKOSConcept(URI.create("http://www.fluffyboards.com/vocabulary#snowboard"));
		assertNotNull(concept);
		assertEquals("http://www.fluffyboards.com/vocabulary#snowboard",concept.getURI().toString());		
	}
	
	@Test
	public void testBroaderThan() throws Exception {					
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.exampleSKOSURI());		
		SKOSHierarchyReader reader = new SKOSHierarchyReader(ontologyManager, ontology);		
		SKOSConcept concept = reader.getSKOSConcept(URI.create("http://www.fluffyboards.com/vocabulary#snowboard"));
		Set<SKOSConcept> concepts = reader.getBroaderThan(concept);
		assertEquals(1,concepts.size());
		SKOSConcept broader = concepts.iterator().next();
		assertEquals("http://www.fluffyboards.com/vocabulary#product",broader.getURI().toString());				
	}
	
	@Test
	public void testBroaderThanCAST() throws Exception {					
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.castSKOSURI());		
		SKOSHierarchyReader reader = new SKOSHierarchyReader(ontologyManager, ontology);		
		SKOSConcept concept = reader.getSKOSConcept(URI.create("http://onto.nerc.ac.uk/CAST/112"));
		Set<SKOSConcept> concepts = reader.getBroaderThan(concept);
		assertEquals(1,concepts.size());
		SKOSConcept broader = concepts.iterator().next();
		assertEquals("http://onto.nerc.ac.uk/CAST/2",broader.getURI().toString());				
	}
	
	@Test
	public void testNarrowerThan() throws Exception {
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.exampleSKOSURI());		
		SKOSHierarchyReader reader = new SKOSHierarchyReader(ontologyManager, ontology);		
		SKOSConcept concept = reader.getSKOSConcept(URI.create("http://www.fluffyboards.com/vocabulary#product"));
		Set<SKOSConcept> concepts = reader.getNarrowerThan(concept);
		assertEquals(1,concepts.size());
		SKOSConcept narrower = concepts.iterator().next();
		assertEquals("http://www.fluffyboards.com/vocabulary#snowboard",narrower.getURI().toString());
	}
	
	@Test
	public void testGetNarrowerDeep() throws Exception {
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.castSKOSURI());		
		SKOSHierarchyReader reader = new SKOSHierarchyReader(ontologyManager, ontology);		
		SKOSConcept concept = reader.getSKOSConcept(URI.create("http://onto.nerc.ac.uk/CAST/5"));
		Set<SKOSConcept> concepts = reader.getNarrowerThan(concept,false);
		assertEquals(20,concepts.size());
		
		concepts = reader.getNarrowerThan(concept,true);
		assertEquals(27,concepts.size());
	}
	
	@Test
	public void testNarrowerThanCAST() throws Exception {
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.castSKOSURI());		
		SKOSHierarchyReader reader = new SKOSHierarchyReader(ontologyManager, ontology);		
		SKOSConcept concept = reader.getSKOSConcept(URI.create("http://onto.nerc.ac.uk/CAST/1"));
		Set<SKOSConcept> concepts = reader.getNarrowerThan(concept);
		assertEquals(68,concepts.size());
		
		concept = reader.getSKOSConcept(URI.create("http://onto.nerc.ac.uk/CAST/2"));
		concepts = reader.getNarrowerThan(concept);
		assertEquals(4,concepts.size());
		
		boolean found=false;
		for (SKOSConcept n : concepts) {
			if (n.getURI().toString().equals("http://onto.nerc.ac.uk/CAST/112")) {
				found=true;
				break;
			}
		}
		assertTrue(found);		
	}
	
	@Test
	public void testSKOSHashSet() throws Exception {
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.exampleSKOSURI());		
		SKOSHierarchyReader reader = new SKOSHierarchyReader(ontologyManager, ontology);		
		SKOSConcept concept = reader.getSKOSConcept(URI.create("http://www.fluffyboards.com/vocabulary#snowboard"));
		SKOSConcept concept2 = reader.getSKOSConcept(URI.create("http://www.fluffyboards.com/vocabulary#snowboard"));
		
		//skos api doesn't regard these as equal
		assertFalse(concept.equals(concept2));
		
		//however, we want our SKOSHashSet to consider them the same
		SKOSHashSet set = new SKOSHashSet();
		assertTrue(set.add(concept));
		assertEquals(1,set.size());
		
		assertTrue(set.contains(concept2));
		assertFalse(set.add(concept2));
		assertEquals(1,set.size());
		assertEquals(concept,set.iterator().next());
	}

}
