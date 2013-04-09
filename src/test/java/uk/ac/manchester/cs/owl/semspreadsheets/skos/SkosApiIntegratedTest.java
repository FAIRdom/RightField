package uk.ac.manchester.cs.owl.semspreadsheets.skos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skosapibinding.SKOSManager;
import org.semanticweb.skosapibinding.SKOSReasoner;
import org.semanticweb.skosapibinding.SKOStoOWLConverter;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.clarkparsia.pellet.owlapiv3.Reasoner;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class SkosApiIntegratedTest {

	@Test
	public void testOpeningSkos() throws Exception {
		SKOSManager manager = new SKOSManager();
		URI uri = DocumentsCatalogue.uriForResourceName("skos/skos-example.rdf");
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
		URI uri = DocumentsCatalogue.uriForResourceName("skos/CAST.rdf");
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
		ontologyManager.loadOntology(DocumentsCatalogue.uriForResourceName("skos/skos-example.rdf"));
		ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		SKOSManager manager = new SKOSManager(owlOntologyManager);
		SKOSConcept concept = manager.getSKOSDataFactory().getSKOSConcept(URI.create("http://www.fluffyboards.com/vocabulary#snowboard"));
		assertNotNull(concept);
		SKOSDataset dataset = (SKOSDataset)manager.getSKOSDataSets().toArray()[1];
		Set<SKOSAnnotation> skosAnnotationsByURI = concept.getSKOSAnnotationsByURI(dataset, URI.create("http://www.w3.org/2004/02/skos/core#broader"));
		assertEquals(1,skosAnnotationsByURI.size());
		assertEquals("http://www.fluffyboards.com/vocabulary#product",skosAnnotationsByURI.iterator().next().getAnnotationValue().getURI().toString());
	}
	
	@Test
	public void testReasoningToGetHierarchy() throws Exception {
		SKOSManager manager = new SKOSManager();
		//manager.loadDatasetFromPhysicalURI(DocumentsCatalogue.uriForResourceName("skos/skos-owl1-dl.rdf"));
		
		URI uri = DocumentsCatalogue.uriForResourceName("skos/skos-example.rdf");
		
		SKOSDataset dataset = manager.loadDataset(uri);
				
				
		SKOStoOWLConverter converter = new SKOStoOWLConverter();
		
		OWLImportsDeclaration importsDec = 
				manager.getOWLManger().getOWLDataFactory().getOWLImportsDeclaration(IRI.create 
				("http://www.w3.org/2004/02/skos/core"));
		
		OWLReasoner r = PelletReasonerFactory.getInstance().createReasoner(converter.getAsOWLOntology(dataset));
		//OWLReasoner r = new Reasoner.ReasonerFactory().createReasoner(converter.getAsOWLOntology(dataset));
		SKOSReasoner reasoner = new SKOSReasoner(manager, r);
		for (SKOSConcept concept : reasoner.getSKOSConcepts()) {
			System.out.println(concept.getURI());
			 for (SKOSConcept broaderCon : 
				 reasoner.getSKOSBroaderTransitiveConcepts(concept)) { 
				                     for (SKOSAnnotation literal : 
				 broaderCon.getSKOSAnnotationsByURI(dataset, 
				 manager.getSKOSDataFactory().getSKOSPrefLabelProperty().getURI())) { 
				                         System.out.println("Narrower concepts: " + 
				 literal.getAnnotationValueAsConstant().getLiteral()); 
				                     }
			 }
//			if (concept.getURI().toString()=="http://www.fluffyboards.com/vocabulary#snowboard") {
//				System.out.println(concept.);
//			}
		}
//		skosAnnotations = reasoner.getSKOSAnnotation(concept, URI.create("http://www.w3.org/2004/02/skos/core#broader"));
//		assertEquals(1,skosAnnotations.size());
//		assertEquals("http://www.fluffyboards.com/vocabulary#product",skosAnnotations.iterator().next().getAnnotationValue().getURI().toString());
//		
//		Set<SKOSConcept> skosBroaderConcepts = reasoner.getSKOSBroaderConcepts(concept);
//		assertEquals(1,skosBroaderConcepts.size());
	}
	
	@Test
	public void testReasoningWithPellet() throws Exception {
		SKOSManager manager = new SKOSManager();
		SKOSDataset skosCoreOntology = manager.loadDataset(DocumentsCatalogue
				.uriForResourceName("skos/skos-owl1-dl.rdf"));
		SKOSDataset dataSet = manager.loadDataset(DocumentsCatalogue
				.uriForResourceName("skos/skos-example.rdf"));
		SKOStoOWLConverter converter = new SKOStoOWLConverter();
		OWLOntology mySkosAsOWLOntology = converter.getAsOWLOntology(dataSet);
		// your skos dataset needs to import the skos core ontology, we do this
		// using the OWL API
		OWLImportsDeclaration importsDec = manager
				.getOWLManger()
				.getOWLDataFactory()
				.getOWLImportsDeclaration(
						IRI.create("http://www.w3.org/2004/02/skos/core"));
		// create the pellet reasoner
		OWLReasoner reasoner = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory()
				.createReasoner(converter.getAsOWLOntology(dataSet));
		// pass the pellet reasoner and the SKOS ontology file to the
		// SKOSReasoner
		SKOSReasoner skosreasoner = new SKOSReasoner(manager, reasoner,
				converter.getAsOWLOntology(skosCoreOntology));
		// example of getting inferred skos:narrowerTransitive concepts
		for (SKOSConcept con : skosreasoner.getSKOSConcepts()) {
			System.out.println("Concept:" + con.getURI());
			for (SKOSConcept broaderCon : skosreasoner
					.getSKOSNarrowerConcepts(con)) {
				for (SKOSAnnotation literal : broaderCon
						.getSKOSAnnotationsByURI(dataSet, manager
								.getSKOSDataFactory()
								.getSKOSPrefLabelProperty().getURI())) {
					System.out.println("Narrower concepts: "
							+ literal.getAnnotationValueAsConstant()
									.getLiteral());
				}
			}
			for (SKOSConcept broaderCon : skosreasoner
					.getSKOSBroaderTransitiveConcepts(con)) {
				for (SKOSAnnotation literal : broaderCon
						.getSKOSAnnotationsByURI(dataSet, manager
								.getSKOSDataFactory()
								.getSKOSPrefLabelProperty().getURI())) {
					System.out.println("Broader concepts: "
							+ literal.getAnnotationValueAsConstant()
									.getLiteral());
				}
			}
		}
	}
}
