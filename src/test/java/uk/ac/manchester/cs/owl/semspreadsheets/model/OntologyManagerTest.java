package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.impl.DummyOntologyManagerListener;

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
	public void testOntologySelectedFired() throws Exception {
		OWLOntology o = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		assertNull(testListener.getOntologySelected());
		ontologyManager.ontologySelected(o);
		assertSame(o,testListener.getOntologySelected());
	}
	
	@Test
	public void differentiatesImports() throws Exception {
		Set<OWLOntology> loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(0,loadedOntologies.size());
		
		ontologyManager.loadOntology(DocumentsCatalogue.scoroOntologyURI());
		
		loadedOntologies = ontologyManager.getLoadedOntologies();
		assertEquals(1,loadedOntologies.size());
		assertEquals(9,ontologyManager.getAllOntologies().size());
		OWLOntology ontology = loadedOntologies.iterator().next();
		assertEquals(IRI.create("http://purl.org/spar/scoro/"),ontology.getOntologyID().getOntologyIRI());
		assertEquals(IRI.create(DocumentsCatalogue.scoroOntologyURI()),ontology.getOntologyID().getVersionIRI());
		assertEquals(IRI.create(DocumentsCatalogue.scoroOntologyURI()),ontology.getOntologyID().getDefaultDocumentIRI());				
		
		//check reasoner includes imported ontologies and can find subclasses and individuals from them
		OWLClass cls = ontologyManager.getDataFactory().getOWLClass(IRI.create("http://www.ontologydesignpatterns.org/cp/owl/situation.owl#Situation"));
		NodeSet<OWLClass> subClasses = ontologyManager.getStructuralReasoner().getSubClasses(cls, false);
		assertFalse(subClasses.isEmpty());
		assertTrue(subClasses.containsEntity(ontologyManager.getDataFactory().getOWLClass(IRI.create("http://purl.org/spar/pro/RoleInTime"))));
		
		cls = ontologyManager.getDataFactory().getOWLClass(IRI.create("http://www.w3.org/2006/time#DayOfWeek"));
		NodeSet<OWLNamedIndividual> instances = ontologyManager.getStructuralReasoner().getInstances(cls, false);
		assertFalse(instances.isEmpty());
		assertTrue(instances.containsEntity(ontologyManager.getDataFactory().getOWLNamedIndividual(IRI.create("http://www.w3.org/2006/time#Friday"))));							
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
		
		assertTrue(testListener.isOntologiesChanedFired());				
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
	
	@Test
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
	
	@Test
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
	
	@Test
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
	
	@Test
	public void includesImportedProperties() throws Exception {
		URI uri = DocumentsCatalogue.scoroOntologyURI();
		ontologyManager.loadOntology(IRI.create(uri));
		Set<OWLPropertyItem> properties = ontologyManager.getAllOWLProperties();
		assertEquals(113,properties.size());
		boolean found=false;
		for (OWLPropertyItem item : properties) {
			if (item.getIRI().toString().equals("http://www.ontologydesignpatterns.org/cp/owl/timeindexedsituation.owl#forEntity")) {
				found=true;
			}			
		}
		assertTrue("Should have found the imported property http://www.ontologydesignpatterns.org/cp/owl/timeindexedsituation.owl#forEntity", found);				
		
		properties = ontologyManager.getOWLObjectProperties();
		assertEquals(76,properties.size());
		
		properties = ontologyManager.getOWLDataProperties();
		assertEquals(37,properties.size());
	}
	
	@Test
	public void testOntologyInUse() throws Exception {
		
		OWLOntology scoroOntology = ontologyManager.loadOntology(DocumentsCatalogue.scoroOntologyURI());
		OWLOntology jermOntology = ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		
		assertEquals("http://purl.org/spar/scoro/",scoroOntology.getOntologyID().getOntologyIRI().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",jermOntology.getOntologyID().getOntologyIRI().toString());
		
		Sheet sheet = workbookManager.getWorkbook().getSheet(0);
		
		Range range  = new Range(sheet,1,1,1,1);
		assertFalse(ontologyManager.isOntologyInUse(jermOntology));
		ontologyManager.setOntologyTermValidation(range, ValidationType.SUBCLASSES, IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#AssayType"), null);
		assertTrue(ontologyManager.isOntologyInUse(jermOntology));
		ontologyManager.remoteOntologyTermValidations(range);
		assertFalse(ontologyManager.isOntologyInUse(jermOntology));
		
		assertFalse(ontologyManager.isOntologyInUse(scoroOntology));
		ontologyManager.setOntologyTermValidation(range,ValidationType.SUBCLASSES,IRI.create("http://www.ontologydesignpatterns.org/cp/owl/situation.owl#Situation"),null);
		assertTrue(ontologyManager.isOntologyInUse(scoroOntology));
		ontologyManager.remoteOntologyTermValidations(range);
		assertFalse(ontologyManager.isOntologyInUse(scoroOntology));
		
		assertFalse(ontologyManager.isOntologyInUse(jermOntology));
		assertFalse(ontologyManager.isOntologyInUse(scoroOntology));
		ontologyManager.setOntologyTermValidation(range, ValidationType.SUBCLASSES, IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#AssayType"), new OWLPropertyItem(IRI.create("http://www.ontologydesignpatterns.org/cp/owl/timeindexedsituation.owl#forEntity"),OWLPropertyType.OBJECT_PROPERTY));
		assertTrue(ontologyManager.isOntologyInUse(jermOntology));
		assertTrue(ontologyManager.isOntologyInUse(scoroOntology));
		ontologyManager.remoteOntologyTermValidations(range);
		assertFalse(ontologyManager.isOntologyInUse(jermOntology));
		assertFalse(ontologyManager.isOntologyInUse(scoroOntology));
		
	}
	
	@Test
	public void testSetOntologyTermValidation() throws Exception {
		ontologyManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		Sheet sheet = workbookManager.getWorkbook().getSheet(0);
		Range range  = new Range(sheet,1,1,1,1);
		assertTrue(ontologyManager.getOntologyIRIs().isEmpty());
		ontologyManager.setOntologyTermValidation(range, ValidationType.SUBCLASSES, IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#AssayType"), null);
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
	public void testLoadEmbeddedOntologies() throws Exception {
		workbookManager.loadWorkbook(DocumentsCatalogue.twoOntologiesWorkbookURI());
		assertEquals(0, ontologyManager.getLoadedOntologies().size());
		assertEquals(0, ontologyManager.getAllOntologies().size());
		assertEquals(2,ontologyManager.getOntologyIRIs().size());
		assertTrue(ontologyManager.getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
		assertTrue(ontologyManager.getOntologyIRIs().contains(IRI.create("http://mged.sourceforge.net/ontologies/MGEDOntology.owl")));
		ontologyManager.loadEmbeddedTermOntologies();
		assertEquals(2, ontologyManager.getLoadedOntologies().size());
		assertEquals(4, ontologyManager.getAllOntologies().size());
		
		//now with just properties over free text		
		WorkbookManager manager = new WorkbookManager();
		manager.loadWorkbook(DocumentsCatalogue.simpleWorkbookWithLiteralsOverRangeURI());
		assertEquals(0, manager.getOntologyManager().getLoadedOntologies().size());
		assertEquals(0, manager.getOntologyManager().getAllOntologies().size());
		assertEquals(1,manager.getOntologyManager().getOntologyIRIs().size());
		manager.getOntologyManager().loadEmbeddedTermOntologies();
		assertEquals(1, manager.getOntologyManager().getLoadedOntologies().size());
		assertEquals(1, manager.getOntologyManager().getAllOntologies().size());
		
		
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
		OWLOntology jermOntology = ontologyManager.loadOntology(IRI.create(DocumentsCatalogue.jermOntologyURI()));
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

}
