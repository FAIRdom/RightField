package uk.ac.manchester.cs.owl.semspreadsheets.model.skos;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class SKOSDetectorTest {

	@Test
	public void testIsSKOS() throws Exception {
		WorkbookManager workbookManager = new WorkbookManager();
		OntologyManager ontologyManager = workbookManager.getOntologyManager();
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		OWLOntology skosDocument = ontologyManager.loadOntology(DocumentsCatalogue.uriForResourceName("skos/skos-example.rdf"));
		OWLOntology castSkosDocument = ontologyManager.loadOntology(DocumentsCatalogue.uriForResourceName("skos/CAST.rdf"));
		
		assertFalse(SKOSDetector.isSKOS(ontology));
		assertTrue(SKOSDetector.isSKOS(skosDocument));
		assertTrue(SKOSDetector.isSKOS(castSkosDocument));
	}
	
	@Test
	public void testIsSKOSEntity() throws Exception {
		WorkbookManager workbookManager = new WorkbookManager();
		OntologyManager ontologyManager = workbookManager.getOntologyManager();
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		OWLOntology skosDocument = ontologyManager.loadOntology(DocumentsCatalogue.uriForResourceName("skos/skos-example.rdf"));
		
		OWLEntity owlEnt = ontology.getEntitiesInSignature(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#affinity_chromatography")).iterator().next();
		OWLEntity skosEnt = skosDocument.getEntitiesInSignature(IRI.create("http://www.fluffyboards.com/vocabulary#snowboard")).iterator().next();
		
		assertTrue(SKOSDetector.isSKOSEntity(skosEnt, ontologyManager));
		assertFalse(SKOSDetector.isSKOSEntity(owlEnt, ontologyManager));
		
		assertTrue(SKOSDetector.isSKOSEntity(skosEnt.getIRI(), ontologyManager));
		assertFalse(SKOSDetector.isSKOSEntity(owlEnt.getIRI(), ontologyManager));
	}

}
