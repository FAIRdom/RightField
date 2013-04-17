package uk.ac.manchester.cs.owl.semspreadsheets.skos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skosapibinding.SKOSManager;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class SkosApiIntegratedTest {

	@Test
	public void testOpeningSkos() throws Exception {
		SKOSManager manager = new SKOSManager();
		URI uri = DocumentsCatalogue.exampleSKOSURI();
		SKOSDataset dataset = manager.loadDataset(uri);
		assertEquals(5,dataset.getSKOSConcepts().size());
		
		SKOSConcept concept = manager.getSKOSDataFactory().getSKOSConcept(URI.create("http://www.fluffyboards.com/vocabulary#snowboard"));
		assertNotNull(concept);
		assertEquals(4,concept.getSKOSAnnotations(dataset).size());
		
		Set<SKOSAnnotation> annotations = concept.getSKOSAnnotationsByURI(dataset, URI.create("http://www.w3.org/2004/02/skos/core#prefLabel"));
		assertEquals(1,annotations.size());
		assertEquals("snowboard",annotations.iterator().next().getAnnotationValueAsConstant().getLiteral());
		
		annotations = concept.getSKOSAnnotationsByURI(dataset, URI.create("http://www.w3.org/2004/02/skos/core#broader"));
		assertEquals(1,annotations.size());
		assertEquals("http://www.fluffyboards.com/vocabulary#product",annotations.iterator().next().getAnnotationValue().getURI().toString());			
	}			
	
	@Test
	public void testOpeningCastSKOS() throws Exception {
		SKOSManager manager = new SKOSManager();
		URI uri = DocumentsCatalogue.castSKOSURI();
		SKOSDataset dataset = manager.loadDataset(uri);
		assertEquals(257,dataset.getSKOSConcepts().size());		
		SKOSConcept concept = manager.getSKOSDataFactory().getSKOSConcept(URI.create("http://onto.nerc.ac.uk/CAST/11"));
		assertNotNull(concept);
		assertEquals(12,concept.getSKOSAnnotations(dataset).size());				
	}
	
	@Test
	public void testLoadingUsignOWLOntologyManager() throws Exception {
		WorkbookManager workbookManager = new WorkbookManager();
		OntologyManager ontologyManager = workbookManager.getOntologyManager();	
		OWLOntologyManager owlOntologyManager = ontologyManager.getOWLOntologyManager();
		ontologyManager.loadOntology(DocumentsCatalogue.exampleSKOSURI());
		ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		SKOSManager manager = new SKOSManager(owlOntologyManager);
		SKOSConcept concept = manager.getSKOSDataFactory().getSKOSConcept(URI.create("http://www.fluffyboards.com/vocabulary#snowboard"));
		assertNotNull(concept);
		SKOSDataset dataset = (SKOSDataset)manager.getSKOSDataSets().toArray()[1];
		Set<SKOSAnnotation> skosAnnotationsByURI = concept.getSKOSAnnotationsByURI(dataset, URI.create("http://www.w3.org/2004/02/skos/core#broader"));
		assertEquals(1,skosAnnotationsByURI.size());
		assertEquals("http://www.fluffyboards.com/vocabulary#product",skosAnnotationsByURI.iterator().next().getAnnotationValue().getURI().toString());
	}
		
}
