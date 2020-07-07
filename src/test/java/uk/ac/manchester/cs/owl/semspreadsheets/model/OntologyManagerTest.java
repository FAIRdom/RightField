package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.DummyOntologyManagerListener;
import uk.ac.manchester.cs.skos.SKOSConceptImpl;

public class OntologyManagerTest {
	
	private OntologyManager ontologyManager;
	private DummyOntologyManagerListener testListener;
	private WorkbookManager workbookManager;

	@Before
	public void createOntologyManager() {
		workbookManager = new WorkbookManager();
		ontologyManager = workbookManager.getOntologyManager();		
		testListener=new DummyOntologyManagerListener();		
		ontologyManager.addListener(testListener);
	}
	
	@Test 
	public void testIsOntologyLoaded() throws Exception {
		OWLOntology jermOnt = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		IRI ontologyIRI = jermOnt.getOntologyID().getOntologyIRI();
		assertTrue(ontologyManager.isOntologyLoaded(ontologyIRI));
		
		ontologyIRI = IRI.create("http://another-ontology.fish");
		assertFalse(ontologyManager.isOntologyLoaded(ontologyIRI));
	}
		
	
	@Test
	public void testGetOntologiesForEntityIRI() throws Exception {
		OWLOntology jermOnt = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());		
		OWLOntology skosOnt = ontologyManager.loadOntology(DocumentsCatalogue.exampleSKOSURI());
		
		OWLEntity ent = jermOnt.getEntitiesInSignature(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#affinity_chromatography")).iterator().next();
		assertNotNull(ent);
		
		Set<OWLOntology> ontologies = ontologyManager.getOntologiesForEntityIRI(ent.getIRI());
		assertEquals(1,ontologies.size());
		assertTrue(ontologies.contains(jermOnt));
		
		ent = skosOnt.getEntitiesInSignature(IRI.create("http://www.fluffyboards.com/vocabulary#snowboard")).iterator().next();
		assertNotNull(ent);
		
		ontologies = ontologyManager.getOntologiesForEntityIRI(ent.getIRI());
		assertEquals(1,ontologies.size());
		assertTrue(ontologies.contains(skosOnt));
		
		ontologies = ontologyManager.getOntologiesForEntityIRI(ent.getIRI(),null);
		assertEquals(1,ontologies.size());
		assertTrue(ontologies.contains(skosOnt));
		
		OWLDataProperty prop = jermOnt.getEntitiesInSignature(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#External_supplier_ID")).iterator().next().asOWLDataProperty();		
		ontologies = ontologyManager.getOntologiesForEntityIRI(ent.getIRI(),new OWLPropertyItem(prop));
		assertEquals(2,ontologies.size());
		assertTrue(ontologies.contains(jermOnt));
		assertTrue(ontologies.contains(skosOnt));
		
		ent = jermOnt.getEntitiesInSignature(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#affinity_chromatography")).iterator().next();
		ontologies = ontologyManager.getOntologiesForEntityIRI(ent.getIRI());
		assertEquals(1,ontologies.size());
		assertTrue(ontologies.contains(jermOnt));		
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
	
	@Test @Ignore("Dependent ontology contains an invalid import. Ontology needs fixing to no longer be reliant on external resources")
	public void loadMGEDOntology() throws Exception {
		//to cover a problem with the MGED ontology loading an ontology with a nil IRI.
		//should be avoided by differntiating between loaded and imported ontologies
		Set<OWLOntology> loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(0,loadedOntologies.size());
		
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.mgedOntologyURI());
		assertNotNull(ontology);
		assertNotNull(ontology.getOntologyID());		
		assertEquals(IRI.create("http://mged.sourceforge.net/ontologies/MGEDOntology.owl"),ontology.getOntologyID().getOntologyIRI());
		
		loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(1,loadedOntologies.size());
		assertEquals(3,ontologyManager.getAllOntologies().size());
		ontology = loadedOntologies.iterator().next();
		assertNotNull(ontology);
		assertNotNull(ontology.getOntologyID());		
		assertEquals(IRI.create("http://mged.sourceforge.net/ontologies/MGEDOntology.owl"),ontology.getOntologyID().getOntologyIRI());
		
		for (OWLOntology o : ontologyManager.getLoadedOntologies()) {
			assertNotNull(o.getOntologyID());
			assertNotNull(o.getOntologyID().getOntologyIRI());
		}
	}
	
	@Test
	public void testOpenRDFSchema() throws Exception {
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.rdfSchemaOntologyURI());
		assertNotNull(ontology.getOntologyID().getOntologyIRI());
		assertTrue(ontologyManager.getLoadedOntologies().contains(ontology));
		assertEquals(1,ontologyManager.getLoadedOntologies().size());
		assertTrue(ontologyManager.getAllOntologies().contains(ontology));		
		
		assertTrue(testListener.isOntologiesChangedFired());
		
	}
	
	@Test
	public void testOntologySelectedFired() throws Exception {
		OWLOntology o = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		assertNull(testListener.getOntologySelected());
		ontologyManager.ontologySelected(o);
		assertSame(o,testListener.getOntologySelected());
	}
	
	@Test @Ignore("Dependent ontology contains an invalid import. Ontology needs fixing to no longer be reliant on external resources")
	public void differentiatesImports() throws Exception {
		Set<OWLOntology> loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(0,loadedOntologies.size());
		
		ontologyManager.loadOntology(DocumentsCatalogue.aminoAcidOntologyURI());
		
		loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(1,loadedOntologies.size());
		assertEquals(2,ontologyManager.getAllOntologies().size());
		OWLOntology ontology = loadedOntologies.iterator().next();
		assertEquals(IRI.create("http://www.co-ode.org/ontologies/amino-acid/2005/10/11/amino-acid.owl"),ontology.getOntologyID().getOntologyIRI());
		assertEquals(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()),ontology.getOntologyID().getVersionIRI());
		assertEquals(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()),ontology.getOntologyID().getDefaultDocumentIRI());				
		
		//check reasoner includes imported ontologies and can find subclasses and individuals from them
		OWLClass cls = ontologyManager.getDataFactory().getOWLClass(IRI.create("http://www.co-ode.org/ontologies/meta/2005/06/15/meta.owl#OWLList"));
		NodeSet<OWLClass> subClasses = ontologyManager.getStructuralReasoner().getSubClasses(cls, false);
		assertFalse(subClasses.isEmpty());
		assertTrue(subClasses.containsEntity(ontologyManager.getDataFactory().getOWLClass(IRI.create("http://www.co-ode.org/ontologies/meta/2005/06/15/meta.owl#EmptyList"))));
		
		cls = ontologyManager.getDataFactory().getOWLClass(IRI.create("http://www.co-ode.org/ontologies/meta/2005/06/15/meta.owl#EmptyList"));
		NodeSet<OWLNamedIndividual> instances = ontologyManager.getStructuralReasoner().getInstances(cls, false);
		assertFalse(instances.isEmpty());
		assertTrue(instances.containsEntity(ontologyManager.getDataFactory().getOWLNamedIndividual(IRI.create("http://www.co-ode.org/ontologies/meta/2005/06/15/meta.owl#nil"))));							
	}		
	
	@Test
	public void testLoadOntology() throws Exception {
		URI uri = DocumentsCatalogue.jermOntologyURI();
		assertEquals(0,ontologyManager.getLoadedOntologies().size());
		OWLOntology ontology = ontologyManager.loadOntology(IRI.create(uri));
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",ontology.getOntologyID().getOntologyIRI().toString());
		
		assertTrue(ontologyManager.getLoadedOntologies().contains(ontology));
		assertEquals(1,ontologyManager.getLoadedOntologies().size());
		assertTrue(ontologyManager.getAllOntologies().contains(ontology));		
		
		assertTrue(testListener.isOntologiesChangedFired());				
	}
	
	@Test
	public void testRemoveOntology() throws Exception {
		Set<OWLOntology> loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(0,loadedOntologies.size());
		assertEquals(0,ontologyManager.getOWLOntologyManager().getOntologies().size());
		OWLOntology ontology = ontologyManager.loadOntology(DocumentsCatalogue.mgedOntologyURI());
						
		loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(1,loadedOntologies.size());
		assertTrue(loadedOntologies.contains(ontology));
		assertTrue(ontologyManager.getAllOntologies().contains(ontology));	
		ontologyManager.removeOntology(ontology);
		
		loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(0,loadedOntologies.size());
		assertFalse(ontologyManager.getAllOntologies().contains(ontology));	
	}
	
	@Test @Ignore("Dependent ontology contains an invalid import. Ontology needs fixing to no longer be reliant on external resources")
	public void testGetDataProperties() throws Exception {		
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.jermOntologyURI()));
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()));
		Set<OWLPropertyItem> dataProperties = ontologyManager.getOWLDataProperties();		
		assertEquals(20,dataProperties.size());
		boolean found=false;
		boolean shouldNotBeFound=false;
		for (OWLPropertyItem property : dataProperties) {
			assertEquals(OWLPropertyType.DATA_PROPERTY,property.getPropertyType());
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#External_supplier_ID")) {
				found=true;
			}
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#technologyUsedIn")) {
				shouldNotBeFound=true;
			}
		}
		assertTrue("Should have found #External_supplier_ID",found);
		assertFalse("Should not have found the #technologyUsedIn as this is an object property",shouldNotBeFound);
	}
	
	@Test
	public void testGetDataPropertiesForASingleOntology() throws Exception {
		OWLOntology jermOntology = ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.jermOntologyURI()));
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()));
		
		Set<OWLPropertyItem> dataProperties = ontologyManager.getOWLDataProperties(jermOntology);		
		assertEquals(19,dataProperties.size());
		boolean found=false;
		boolean shouldNotBeFound=false;
		for (OWLPropertyItem property : dataProperties) {
			assertEquals(OWLPropertyType.DATA_PROPERTY,property.getPropertyType());
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#External_supplier_ID")) {
				found=true;
			}
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#technologyUsedIn")) {
				shouldNotBeFound=true;
			}
		}
		assertTrue("Should have found #External_supplier_ID",found);
		assertFalse("Should not have found the #technologyUsedIn as this is an object property",shouldNotBeFound);
	}
	
	@Test @Ignore("Dependent ontology contains an invalid import. Ontology needs fixing to no longer be reliant on external resources")
	public void testGetObjectProperties() throws Exception {
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.jermOntologyURI()));
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()));
		Set<OWLPropertyItem> objectProperties = ontologyManager.getOWLObjectProperties();
		
		assertEquals(32,objectProperties.size());
		boolean found=false;
		boolean shouldNotBeFound=false;
		for (OWLPropertyItem property : objectProperties) {	
			assertEquals(OWLPropertyType.OBJECT_PROPERTY,property.getPropertyType());
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#technologyUsedIn")) {
				found=true;
			}
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#External_supplier_ID")) {
				shouldNotBeFound=true;
			}
		}
		assertTrue("Should have found #technologyUsedIn",found);
		assertFalse("Should not have found #External_supplier_ID  as this is a data property",shouldNotBeFound);
	}
	
	@Test
	public void testGetObjectPropertiesForASingleOntology() throws Exception {
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.jermOntologyURI()));
		OWLOntology aminoAcidOntology = ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()));
		Set<OWLPropertyItem> objectProperties = ontologyManager.getOWLObjectProperties(aminoAcidOntology);
		
		assertEquals(5,objectProperties.size());
		boolean found=false;
		boolean shouldNotBeFound=false;
		for (OWLPropertyItem property : objectProperties) {	
			assertEquals(OWLPropertyType.OBJECT_PROPERTY,property.getPropertyType());
			if (property.getIRI().toString().equals("http://www.co-ode.org/ontologies/amino-acid/2005/10/11/amino-acid.owl#hasSideChainStructure")) {
				found=true;
			}
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#technologyUsedIn")) {
				shouldNotBeFound=true;
			}
		}
		assertTrue("Should have found #hasSideChainStructure",found);
		assertFalse("Should not have found #JERMOntology#technologyUsedIn  as this is a data property",shouldNotBeFound);
	}
	
	@Test @Ignore("Dependent ontology contains an invalid import. Ontology needs fixing to no longer be reliant on external resources")
	public void getAllOWLProperties() throws Exception {
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.jermOntologyURI()));
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()));
		Set<OWLPropertyItem> properties = ontologyManager.getAllOWLProperties();
		assertEquals(52,properties.size());
		boolean found=false;
		boolean found2=false;
		boolean found3=false;
		for (OWLPropertyItem property : properties) {	
			assertTrue(property.getPropertyType().equals(OWLPropertyType.DATA_PROPERTY) || property.getPropertyType().equals(OWLPropertyType.OBJECT_PROPERTY));
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#technologyUsedIn")) {
				found=true;
			}
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#External_supplier_ID")) {
				found2=true;
			}
			if (property.getIRI().toString().equals("http://www.co-ode.org/ontologies/amino-acid/2005/10/11/amino-acid.owl#hasSideChainStructure")) {
				found3=true;
			}
		}
		assertTrue("Should have found JERMOntology#technologyUsedIn",found);
		assertTrue("Should have found JERMOntology#External_supplier_ID",found2);
		assertTrue("Should have found amino-acid.owl#hasSideChainStructure",found3);
	}
	
	@Test
	public void getAllOWLPropertiesForASingleOntology() throws Exception {
		OWLOntology jermOntology = ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.jermOntologyURI()));
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()));
		Set<OWLPropertyItem> properties = ontologyManager.getAllOWLProperties(jermOntology);
		assertEquals(37,properties.size());
		boolean found=false;
		boolean found2=false;
		boolean shoudNotBeFound=false;
		for (OWLPropertyItem property : properties) {	
			assertTrue(property.getPropertyType().equals(OWLPropertyType.DATA_PROPERTY) || property.getPropertyType().equals(OWLPropertyType.OBJECT_PROPERTY));
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#technologyUsedIn")) {
				found=true;
			}
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#External_supplier_ID")) {
				found2=true;
			}
			if (property.getIRI().toString().equals("http://www.co-ode.org/ontologies/amino-acid/2005/10/11/amino-acid.owl#hasSideChainStructure")) {
				shoudNotBeFound=true;
			}
		}
		assertTrue("Should have found JERMOntology#technologyUsedIn",found);
		assertTrue("Should have found JERMOntology#External_supplier_ID",found2);
		assertFalse("The property amino-acid.owl#hasSideChainStructure should not have been found",shoudNotBeFound);
	}
	
	@Test @Ignore("Dependent ontology contains an invalid import. Ontology needs fixing to no longer be reliant on external resources")
	public void includesImportedProperties() throws Exception {
		URI uri = DocumentsCatalogue.aminoAcidOntologyURI();
		ontologyManager.loadOntology(IRI.create(uri));
		Set<OWLPropertyItem> properties = ontologyManager.getAllOWLProperties();
		
		boolean found=false;
		for (OWLPropertyItem item : properties) {
			if (item.getIRI().toString().equals("http://www.co-ode.org/ontologies/meta/2005/06/15/meta.owl#hasBeenClassified")) {
				found=true;
			}			
		}
		assertTrue("Should have found the imported property http://www.co-ode.org/ontologies/meta/2005/06/15/meta.owl#hasBeenClassified", found);								
	}
	
	@Test
	public void testOntologyInUse() throws Exception {
		
		OWLOntology aminoAcidOntology = ontologyManager.loadOntology(DocumentsCatalogue.aminoAcidOntologyURI());
		OWLOntology jermOntology = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		
		assertEquals("http://www.co-ode.org/ontologies/amino-acid/2005/10/11/amino-acid.owl",aminoAcidOntology.getOntologyID().getOntologyIRI().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",jermOntology.getOntologyID().getOntologyIRI().toString());
		
		Sheet sheet = workbookManager.getWorkbook().getSheet(0);
		
		Range range  = new Range(sheet,1,1,1,1);
		assertFalse(ontologyManager.isOntologyInUse(jermOntology));
		ontologyManager.setOntologyTermValidation(range, ValidationType.SUBCLASSES, IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#AssayType"), null);
		assertTrue(ontologyManager.isOntologyInUse(jermOntology));
		ontologyManager.remoteOntologyTermValidations(range);
		assertFalse(ontologyManager.isOntologyInUse(jermOntology));
		
		assertFalse(ontologyManager.isOntologyInUse(aminoAcidOntology));
		ontologyManager.setOntologyTermValidation(range,ValidationType.SUBCLASSES,IRI.create("http://www.co-ode.org/ontologies/amino-acid/2005/10/11/amino-acid.owl#Hydrophobic"),null);
		assertTrue(ontologyManager.isOntologyInUse(aminoAcidOntology));
		ontologyManager.remoteOntologyTermValidations(range);
		assertFalse(ontologyManager.isOntologyInUse(aminoAcidOntology));
		
		assertFalse(ontologyManager.isOntologyInUse(jermOntology));
		assertFalse(ontologyManager.isOntologyInUse(aminoAcidOntology));
		ontologyManager.setOntologyTermValidation(range, ValidationType.SUBCLASSES, IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#AssayType"), new OWLPropertyItem(IRI.create("http://www.co-ode.org/ontologies/amino-acid/2005/10/11/amino-acid.owl#hasCharge"),OWLPropertyType.OBJECT_PROPERTY));
		assertTrue(ontologyManager.isOntologyInUse(jermOntology));
		assertTrue(ontologyManager.isOntologyInUse(aminoAcidOntology));
		ontologyManager.remoteOntologyTermValidations(range);
		assertFalse(ontologyManager.isOntologyInUse(jermOntology));
		assertFalse(ontologyManager.isOntologyInUse(aminoAcidOntology));
		
	}
	
	@Test
	public void testSetOntologyTermValidation() throws Exception {
		ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		Sheet sheet = workbookManager.getWorkbook().getSheet(0);
		Range range  = new Range(sheet,1,1,1,1);
		assertTrue(ontologyManager.getOntologyIRIs().isEmpty());
		ontologyManager.setOntologyTermValidation(range, ValidationType.SUBCLASSES, IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#AssayType"), null, null);
		assertEquals(1,ontologyManager.getOntologyIRIs().size());
		assertTrue(ontologyManager.getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
		
		ontologyManager.remoteOntologyTermValidations(range);
		assertTrue(ontologyManager.getOntologyIRIs().isEmpty());
		
		//with free text and just a property
		ontologyManager.setOntologyTermValidation(range, ValidationType.FREETEXT, null, new OWLPropertyItem(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#hasType"),OWLPropertyType.DATA_PROPERTY));
		assertEquals(1,ontologyManager.getOntologyIRIs().size());
		assertTrue(ontologyManager.getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
		
		ontologyManager.remoteOntologyTermValidations(range);
		assertTrue(ontologyManager.getOntologyIRIs().isEmpty());
		
	}
	

	
	@Test
	public void testGetPropertiesForSubclasses() throws Exception {
		OWLOntology jermOntology = ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.jermOntologyURI()));
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()));
		Set<OWLPropertyItem> properties = ontologyManager.getAllOWLProperties(jermOntology,ValidationType.SUBCLASSES);
		assertEquals(properties,ontologyManager.getAllOWLProperties(jermOntology,ValidationType.DIRECTSUBCLASSES));
		assertEquals(37,properties.size());
		boolean found=false;
		for (OWLPropertyItem item : properties) {
			assertTrue(OWLPropertyType.DATA_PROPERTY==item.getPropertyType() || OWLPropertyType.OBJECT_PROPERTY==item.getPropertyType());
			if (item.getIRI().equals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#Lab_internal_ID"))) {
				found=true;
			}
		}
		assertTrue("Should have found http://www.mygrid.org.uk/ontology/JERMOntology#Lab_internal_ID",found);
	}
	
	@Test
	public void testGetPropertiesForIndividuals() throws Exception {
		OWLOntology jermOntology = ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.jermOntologyURI()));
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()));
		Set<OWLPropertyItem> properties = ontologyManager.getAllOWLProperties(jermOntology,ValidationType.INDIVIDUALS);
		assertEquals(properties,ontologyManager.getAllOWLProperties(jermOntology,ValidationType.DIRECTINDIVIDUALS));
		assertEquals(37,properties.size());
		boolean found=false;
		for (OWLPropertyItem item : properties) {
			assertTrue(OWLPropertyType.DATA_PROPERTY==item.getPropertyType() || OWLPropertyType.OBJECT_PROPERTY==item.getPropertyType());
			if (item.getIRI().equals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#Lab_internal_ID"))) {
				found=true;
			}
		}
		assertTrue("Should have found http://www.mygrid.org.uk/ontology/JERMOntology#Lab_internal_ID",found);
	}
	
	@Test
	public void testGetPropertiesForFreeText() throws Exception {
		OWLOntology jermOntology = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.aminoAcidOntologyURI()));
		Set<OWLPropertyItem> properties = ontologyManager.getAllOWLProperties(jermOntology,ValidationType.FREETEXT);
		assertEquals(19,properties.size());
		boolean found=false;
		
		for (OWLPropertyItem item : properties) {
			assertEquals(OWLPropertyType.DATA_PROPERTY,item.getPropertyType());
			if (item.getIRI().equals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#Lab_internal_ID"))) {
				found=true;
			}
		}
		assertTrue("Should have found http://www.mygrid.org.uk/ontology/JERMOntology#Lab_internal_ID",found);
	}
	
	@Test
	public void testSearchByMatchingLabel() throws Exception {
		ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		ontologyManager.loadOntology(DocumentsCatalogue.exampleSKOSURI());
		ontologyManager.loadOntology(DocumentsCatalogue.castSKOSURI());
		
		//search for some terms from JERM
		Collection<OWLEntity> results = ontologyManager.searchForMatchingEntitiesByLabel("metabolomics");
		assertEquals(1,results.size());
		assertTrue(results.contains(new OWLClassImpl(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#metabolomics"))));
		
		results = ontologyManager.searchForMatchingEntitiesByLabel("mEtabOLomicS");
		assertEquals(1,results.size());
		assertTrue(results.contains(new OWLClassImpl(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#metabolomics"))));
		
		results = ontologyManager.searchForMatchingEntitiesByLabel("etabol");
		assertEquals(8,results.size());
		assertTrue(results.contains(new OWLClassImpl(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#metabolomics"))));
		assertTrue(results.contains(new OWLClassImpl(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#metabolite_profiling"))));
		
		//now some SKOS terms
		results = ontologyManager.searchForMatchingEntitiesByLabel("Customer");
		assertEquals(1,results.size());
		assertTrue(results.contains(new OWLNamedIndividualImpl(IRI.create("http://www.fluffyboards.com/vocabulary#customer"))));			
		
		results = ontologyManager.searchForMatchingEntitiesByLabel("eview");
		assertEquals(1,results.size());
		assertTrue(results.contains(new OWLNamedIndividualImpl(IRI.create("http://www.fluffyboards.com/vocabulary#review"))));
		
		//check its using the label and not just the URI
		results = ontologyManager.searchForMatchingEntitiesByLabel("111");
		assertEquals(0,results.size());
		results = ontologyManager.searchForMatchingEntitiesByLabel("water depth");
		assertEquals(1,results.size());
		assertTrue(results.contains(new OWLNamedIndividualImpl(IRI.create("http://onto.nerc.ac.uk/CAST/187"))));
		
		//include altLabel
		results = ontologyManager.searchForMatchingEntitiesByLabel("product opinion");
		assertEquals(1,results.size());
		assertTrue(results.contains(new OWLNamedIndividualImpl(IRI.create("http://www.fluffyboards.com/vocabulary#review"))));		
	}
	
	@Test
	public void testGetRendering() throws Exception {
		ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		ontologyManager.loadOntology(DocumentsCatalogue.castSKOSURI());
		
		String rendering = ontologyManager.getRendering(new OWLClassImpl(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#metabolite_profiling")));
		assertEquals("metabolite_profiling",rendering);
		
		rendering = ontologyManager.getRendering(new SKOSConceptImpl(new OWLNamedIndividualImpl(IRI.create("http://onto.nerc.ac.uk/CAST/178"))));
		assertEquals("ground level rainfall measurement",rendering);
		
		//try sending a SKOS term, but as an OWLEntity
		rendering = ontologyManager.getRendering(new OWLNamedIndividualImpl(IRI.create("http://onto.nerc.ac.uk/CAST/178")));
		assertEquals("ground level rainfall measurement",rendering);
		
	}

}
