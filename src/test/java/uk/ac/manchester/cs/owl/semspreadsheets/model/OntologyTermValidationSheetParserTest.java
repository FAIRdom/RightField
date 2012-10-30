package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.SheetHSSFImpl;

public class OntologyTermValidationSheetParserTest {

	@Test
	public void testValidationDescriptor() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		SheetHSSFImpl sheet = SpreadsheetTestHelper.getWorkbookSheetHSSF(DocumentsCatalogue.bookWithPropertiesURI(),1);		
		OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(manager, sheet);
		OntologyTermValidationDescriptor validationDescriptor = parser.parseValidationDescriptor();
		assertNotNull(validationDescriptor);
		assertEquals(ValidationType.SUBCLASSES,validationDescriptor.getType());
		assertEquals(1,validationDescriptor.getOntologyIRIs().size());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",validationDescriptor.getOntologyIRIs().iterator().next().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#TechnologyType",validationDescriptor.getEntityIRI().toString());
		assertEquals(60,validationDescriptor.getTerms().size());
		assertEquals(OWLPropertyType.OBJECT_PROPERTY,validationDescriptor.getOWLPropertyItem().getPropertyType());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#hasType",validationDescriptor.getOWLPropertyItem().getIRI().toString());
		
		assertEquals(1,validationDescriptor.getOntologyIRIs().size());
		assertTrue(validationDescriptor.getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
	}

	@Test
	public void testValidationDescriptorNoProperty() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		SheetHSSFImpl sheet = SpreadsheetTestHelper.getWorkbookSheetHSSF(DocumentsCatalogue.twoOntologiesWorkbookURI(),1);		
		OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(manager, sheet);
		OntologyTermValidationDescriptor validationDescriptor = parser.parseValidationDescriptor();
		assertNotNull(validationDescriptor);
		assertEquals(ValidationType.INDIVIDUALS,validationDescriptor.getType());
		assertEquals("http://mged.sourceforge.net/ontologies/MGEDOntology.owl#DerivedBioAssayType",validationDescriptor.getEntityIRI().toString());
		assertNull(validationDescriptor.getOWLPropertyItem());
		assertEquals(1,validationDescriptor.getOntologyIRIs().size());
		assertTrue(validationDescriptor.getOntologyIRIs().contains(IRI.create("http://mged.sourceforge.net/ontologies/MGEDOntology.owl")));
	}	
	
	@Test
	public void testValidationDescriptorFreeTextWithProperty() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		SheetHSSFImpl sheet = SpreadsheetTestHelper.getWorkbookSheetHSSF(DocumentsCatalogue.simpleWorkbookWithLiteralsOverRangeURI(),1);	
		OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(manager, sheet);
		OntologyTermValidationDescriptor validationDescriptor = parser.parseValidationDescriptor();
		assertNotNull(validationDescriptor);
		assertEquals(ValidationType.FREETEXT,validationDescriptor.getType());
		
		assertNotNull(validationDescriptor.getOWLPropertyItem());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber",validationDescriptor.getOWLPropertyItem().getIRI().toString());
		assertEquals(OWLPropertyType.DATA_PROPERTY,validationDescriptor.getOWLPropertyItem().getPropertyType());
		assertEquals(1,validationDescriptor.getOntologyIRIs().size());
		assertTrue(validationDescriptor.getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
		
	}
}
