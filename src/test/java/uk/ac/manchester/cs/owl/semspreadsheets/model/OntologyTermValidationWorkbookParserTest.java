package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.impl.SheetHSSFImpl;

public class OntologyTermValidationWorkbookParserTest {

	@Test
	public void testValidationDescriptor() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		SheetHSSFImpl sheet = SpreadsheetTestHelper.getWorkbookSheet("book_with_properties.xls",1);		
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
	}

	@Test
	public void testValidationDescriptorNoProperty() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		SheetHSSFImpl sheet = SpreadsheetTestHelper.getWorkbookSheet("two_ontologies.xls",1);		
		OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(manager, sheet);
		OntologyTermValidationDescriptor validationDescriptor = parser.parseValidationDescriptor();
		assertNotNull(validationDescriptor);
		assertEquals(ValidationType.INDIVIDUALS,validationDescriptor.getType());
		assertEquals("http://mged.sourceforge.net/ontologies/MGEDOntology.owl#DerivedBioAssayType",validationDescriptor.getEntityIRI().toString());
		assertNull(validationDescriptor.getOWLPropertyItem());
	}	
}
