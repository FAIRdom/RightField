package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.reasoner.impl.NodeFactory;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;

public class OntologyTermValidationDescriptorTest {
	
	private OntologyManager ontManager;

	@Before
	public void setup() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		ontManager = manager.getOntologyManager();
		ontManager.loadOntology(DocumentsCatalogue.jermOntologyURI().toURL().toURI());
		ontManager.loadOntology(DocumentsCatalogue.aminoAcidOntologyURI());		
	}
	
	@Test
	public void testOntologyIRIsWithOWLThing() throws Exception {		
		IRI owlThingEntity = NodeFactory.getOWLClassTopNode().getEntities().iterator().next().getIRI();
		OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(ValidationType.SUBCLASSES,owlThingEntity,null,ontManager);
		assertEquals(3,descriptor.getOntologyIRIs().size());
		assertTrue(descriptor.getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
		assertTrue(descriptor.getOntologyIRIs().contains(IRI.create("http://www.co-ode.org/ontologies/meta/2005/06/15/meta.owl")));
		assertTrue(descriptor.getOntologyIRIs().contains(IRI.create("http://www.co-ode.org/ontologies/amino-acid/2005/10/11/amino-acid.owl")));
	}		
	
	@Test
	public void testOntologyIRIsWithOWLThingAndRDFS() throws Exception {
		OntologyManager man = new WorkbookManager().getOntologyManager();
		man.loadOntology(DocumentsCatalogue.rdfSchemaOntologyURI());
		IRI owlThingEntity = NodeFactory.getOWLClassTopNode().getEntities().iterator().next().getIRI();
		OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(ValidationType.SUBCLASSES,owlThingEntity,null,man);
		assertEquals(1,descriptor.getOntologyIRIs().size());
		assertTrue(descriptor.getOntologyIRIs().contains(IRI.create(DocumentsCatalogue.rdfSchemaOntologyURI())));		
	}

	@Test
	public void testPropertyOnly() throws Exception {		
		
		OWLPropertyItem property = new OWLPropertyItem(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber"),OWLPropertyType.DATA_PROPERTY);
		OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(property, ontManager);
		
		
		assertEquals(ValidationType.FREETEXT,descriptor.getType());
		assertEquals(property,descriptor.getOWLPropertyItem());
		assertTrue(descriptor.getTerms().isEmpty());
		assertEquals(IRI.create("http://www.w3.org/2002/07/owl#Nothing"),descriptor.getEntityIRI());		
		assertNotNull(descriptor.hashCode());
		assertTrue(descriptor.definesLiteral());
		assertEquals(1,descriptor.getOntologyIRIs().size());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",descriptor.getOntologyIRIs().iterator().next().toString());
		
		assertEquals(1,descriptor.getOntologyIRIs().size());
		assertTrue(descriptor.getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
	}
	
	@Test
	public void testEntityNoProperty() throws Exception {		
		
		IRI entityIRI = IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#Project");
		
		OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(ValidationType.INDIVIDUALS,entityIRI,null,ontManager);
		
		assertEquals(ValidationType.INDIVIDUALS,descriptor.getType());
		assertNull(descriptor.getOWLPropertyItem());
		assertEquals(13,descriptor.getTerms().size());
		assertEquals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#Project"),descriptor.getEntityIRI());		
		assertNotNull(descriptor.hashCode());
		assertFalse(descriptor.definesLiteral());
		assertEquals(1,descriptor.getOntologyIRIs().size());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",descriptor.getOntologyIRIs().iterator().next().toString());
		
		assertEquals(1,descriptor.getOntologyIRIs().size());
		assertTrue(descriptor.getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
	}
	
	@Test
	public void testEntityAndProperty() throws Exception {		
		
		OWLPropertyItem property = new OWLPropertyItem(IRI.create("http://mygrid/JERMOntology#fishing"),OWLPropertyType.DATA_PROPERTY);
		IRI entityIRI = IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#AssayType");
		
		OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(ValidationType.SUBCLASSES,entityIRI,property,ontManager);
		
		assertEquals(ValidationType.SUBCLASSES,descriptor.getType());
		assertEquals(65,descriptor.getTerms().size());
		assertEquals(property,descriptor.getOWLPropertyItem());
		
		assertEquals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#AssayType"),descriptor.getEntityIRI());		
		assertNotNull(descriptor.hashCode());
		assertFalse(descriptor.definesLiteral());
		assertEquals(1,descriptor.getOntologyIRIs().size());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",descriptor.getOntologyIRIs().iterator().next().toString());
		
		assertEquals(1,descriptor.getOntologyIRIs().size());
		assertTrue(descriptor.getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
	}		

}
