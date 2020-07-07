package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;

public class OntologyTermValidationWorkbookParserTest {

	private OntologyTermValidationWorkbookParser parser;
	private WorkbookManager manager;
	private OntologyManager ontManager;

	@Before
	public void setup() throws Exception {
		manager = new WorkbookManager();
		ontManager = manager.getOntologyManager();
		ontManager.loadOntology(DocumentsCatalogue.jermOntologyURI());
		parser = new OntologyTermValidationWorkbookParser(manager);
	}

	@Test
	public void testWritingFreeTextProperty() throws Exception {
		assertEquals(1,manager.getWorkbook().getSheets().size());
		assertEquals(1,manager.getWorkbook().getVisibleSheets().size());

		Collection<OntologyTermValidation> validations = createFreeTextValidations();

		Collection<Validation> sheetValidations = manager.getWorkbook().getSheet(0).getValidations();
		assertEquals(0,sheetValidations.size());

		parser.writeOntologyTermValidations(validations);

		assertEquals(2,manager.getWorkbook().getSheets().size());
		assertEquals(1,manager.getWorkbook().getVisibleSheets().size());
		Sheet validationSheet = manager.getWorkbook().getSheet(1);
		assertTrue(validationSheet.isVeryHidden());
		assertEquals("wksowlv0",validationSheet.getName());

		assertEquals("FREETEXT",validationSheet.getCellAt(0,0).getValue());
		assertEquals("<http://www.w3.org/2002/07/owl#Nothing>",validationSheet.getCellAt(1,0).getValue());
		assertEquals("ontology",validationSheet.getCellAt(0,1).getValue());
		assertEquals("<http://www.mygrid.org.uk/ontology/JERMOntology>",validationSheet.getCellAt(1,1).getValue());
		assertEquals("<"+DocumentsCatalogue.jermOntologyURI().toString()+">",validationSheet.getCellAt(2,1).getValue());
		assertNull(validationSheet.getCellAt(1,2));

		sheetValidations = manager.getWorkbook().getSheet(0).getValidations();
		assertEquals(1,sheetValidations.size());
		Validation v = sheetValidations.iterator().next();
		Sheet sheet = manager.getWorkbook().getSheet(0);
		Cell cell = sheet.getCellAt(2, 1);
		assertEquals(sheet,v.getSheet());
		assertEquals(new Range(sheet,cell),v.getRange());
		assertEquals("AND(A1<>\"propliteral^wksowlv0\")",v.getFormula());
		assertTrue(v.isLiteralValidation());
		assertFalse(v.isDataValidation());
	}

	private Collection<OntologyTermValidation> createFreeTextValidations() {
		Sheet sheet = manager.getWorkbook().getSheet(0);
		assertNotNull(sheet);
		assertEquals("Sheet0",sheet.getName());
		OWLPropertyItem property = new OWLPropertyItem(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#hasCharacteristics"),OWLPropertyType.OBJECT_PROPERTY);
		assertNotNull(property);

		Cell cell = sheet.addCellAt(2, 1);
		assertNotNull(cell);
		Range range = new Range(sheet, cell);

		OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(property, ontManager);
		OntologyTermValidation validation = new OntologyTermValidation(descriptor, range);
		ArrayList<OntologyTermValidation> validations = new ArrayList<OntologyTermValidation>();
		validations.add(validation);
		return validations;
	}

	@Test
	public void testWritingDataSelectionValidation() throws Exception {

		assertEquals(1,manager.getWorkbook().getSheets().size());
		assertEquals(1,manager.getWorkbook().getVisibleSheets().size());


		Collection<Validation> sheetValidations = manager.getWorkbook().getSheet(0).getValidations();
		assertEquals(0,sheetValidations.size());
		Collection<OntologyTermValidation> validations = createDataSelectionValidation();
		parser.writeOntologyTermValidations(validations);

		assertEquals(2,manager.getWorkbook().getSheets().size());
		assertEquals(1,manager.getWorkbook().getVisibleSheets().size());
		Sheet validationSheet = manager.getWorkbook().getSheet(1);
		assertTrue(validationSheet.isVeryHidden());
		assertEquals("wksowlv0",validationSheet.getName());

		assertEquals("INDIVIDUALS",validationSheet.getCellAt(0,0).getValue());
		assertEquals("<http://www.mygrid.org.uk/ontology/JERMOntology#Project>",validationSheet.getCellAt(1,0).getValue());
		assertEquals("ontology",validationSheet.getCellAt(0,1).getValue());
		assertEquals("<http://www.mygrid.org.uk/ontology/JERMOntology>",validationSheet.getCellAt(1,1).getValue());
		assertEquals("<"+DocumentsCatalogue.jermOntologyURI().toString()+">",validationSheet.getCellAt(2,1).getValue());
		assertEquals("<http://www.mygrid.org.uk/ontology/JERMOntology#BaCell>",validationSheet.getCellAt(0, 2).getValue());
		assertEquals("BaCell",validationSheet.getCellAt(1, 2).getValue());
		sheetValidations = manager.getWorkbook().getSheet(0).getValidations();

		Validation v = sheetValidations.iterator().next();
		assertEquals(1,sheetValidations.size());
		Sheet sheet = manager.getWorkbook().getSheet(0);
		Cell cell = sheet.getCellAt(2, 1);
		assertEquals(sheet,v.getSheet());
		assertEquals(new Range(sheet,cell),v.getRange());
		assertEquals("wksowlv0",v.getFormula());
		assertTrue(v.isDataValidation());
		assertFalse(v.isLiteralValidation());
	}

	private Collection<OntologyTermValidation> createDataSelectionValidation() throws Exception {
		Sheet sheet = manager.getWorkbook().getSheet(0);
		assertNotNull(sheet);
		assertEquals("Sheet0",sheet.getName());
		OWLPropertyItem property = new OWLPropertyItem(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#hasCharacteristics"),OWLPropertyType.OBJECT_PROPERTY);
		assertNotNull(property);

		Cell cell = sheet.addCellAt(2, 1);
		assertNotNull(cell);
		Range range = new Range(sheet, cell);


		ValidationType validationType = ValidationType.INDIVIDUALS;
		IRI iri = IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#Project");

		OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(validationType,iri,property,
				validationType.getTerms(ontManager, iri), ontManager);
		OntologyTermValidation validation = new OntologyTermValidation(descriptor, range);
		ArrayList<OntologyTermValidation> validations = new ArrayList<OntologyTermValidation>();
		validations.add(validation);
		return validations;
	}

	@Test
	public void testParseDataSelectionValidation() throws Exception {
		Collection<OntologyTermValidation> validations = createDataSelectionValidation();
		parser.writeOntologyTermValidations(validations);
		validations = parser.readOntologyTermValidations();
		assertEquals(1,validations.size());
		OntologyTermValidation v = validations.iterator().next();
		assertEquals(new Range(manager.getWorkbook().getSheet(0),2,1,2,1),v.getRange());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#Project",v.getValidationDescriptor().getEntityIRI().toString());
		assertEquals(ValidationType.INDIVIDUALS,v.getValidationDescriptor().getType());
		assertNotNull(v.getValidationDescriptor().getOWLPropertyItem());
		assertEquals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#hasCharacteristics"),v.getValidationDescriptor().getOWLPropertyItem().getIRI());
		assertEquals(OWLPropertyType.OBJECT_PROPERTY,v.getValidationDescriptor().getOWLPropertyItem().getPropertyType());
		assertEquals(1,v.getValidationDescriptor().getOntologyIRIs().size());
		assertTrue(v.getValidationDescriptor().getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
	}

	@Test
	public void testParseFreeTextProperty() throws Exception {
		Collection<OntologyTermValidation> validations = createFreeTextValidations();
		parser.writeOntologyTermValidations(validations);
		validations = parser.readOntologyTermValidations();
		assertEquals(1,validations.size());
		OntologyTermValidation v = validations.iterator().next();
		assertEquals(new Range(manager.getWorkbook().getSheet(0),2,1,2,1),v.getRange());
		assertEquals("http://www.w3.org/2002/07/owl#Nothing",v.getValidationDescriptor().getEntityIRI().toString());
		assertEquals(ValidationType.FREETEXT,v.getValidationDescriptor().getType());
		assertEquals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#hasCharacteristics"),v.getValidationDescriptor().getOWLPropertyItem().getIRI());
		assertEquals(OWLPropertyType.OBJECT_PROPERTY,v.getValidationDescriptor().getOWLPropertyItem().getPropertyType());
		assertEquals(1,v.getValidationDescriptor().getOntologyIRIs().size());
		assertTrue(v.getValidationDescriptor().getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
	}

	@Test //covers an error when trying to read the data validations, where it detects a formula but it is blank. Possibly caused by macros.
	public void testParseValidationsWithBlankFormulaXLS() throws Exception {
		Workbook book = manager.loadWorkbook(DocumentsCatalogue.prideTemplateEmptyWorkbookURI());
		assertEquals(15,book.getSheets().size());
		assertEquals("Introduction",book.getSheet(0).getName());
		Collection<OntologyTermValidation> validations = parser.readOntologyTermValidations();
		assertEquals(0,validations.size());
	}

	@Test //same as test above but for xlsx
	public void testParseValidationsWithBlankFormulaXLSX() throws Exception {
		Workbook book = manager.loadWorkbook(DocumentsCatalogue.prideTemplateEmptyWorkbookXLSXURI());
		assertEquals(15,book.getSheets().size());
		assertEquals("Introduction",book.getSheet(0).getName());
		Collection<OntologyTermValidation> validations = parser.readOntologyTermValidations();
		assertEquals(0,validations.size());
	}

	@Test
	public void testParseFreeTextPropertyValidationFromFile() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		//parser is invoked during load, and then the validations are stripped out of the workbook, but stored by the ontologyTermManager
		manager.loadWorkbook(DocumentsCatalogue.simpleWorkbookWithLiteralsOverRangeURI());
		Collection<OntologyTermValidation> validations = manager.getOntologyManager().getOntologyTermValidations();

		assertEquals(1,validations.size());
		OntologyTermValidation v = validations.iterator().next();
		assertEquals(new Range(manager.getWorkbook().getSheet(0),1,1,3,4),v.getRange());
		assertEquals("http://www.w3.org/2002/07/owl#Nothing",v.getValidationDescriptor().getEntityIRI().toString());
		assertEquals(ValidationType.FREETEXT,v.getValidationDescriptor().getType());
		assertEquals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber"),v.getValidationDescriptor().getOWLPropertyItem().getIRI());
		assertEquals(OWLPropertyType.DATA_PROPERTY,v.getValidationDescriptor().getOWLPropertyItem().getPropertyType());
		assertEquals(1,v.getValidationDescriptor().getOntologyIRIs().size());
		assertTrue(v.getValidationDescriptor().getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));

	}

	@Test
	public void testClearOntologyTermValidations() throws Exception {
		Collection<OntologyTermValidation> validations = createFreeTextValidations();
		validations.addAll(createDataSelectionValidation());
		parser.writeOntologyTermValidations(validations);
		assertEquals(2,parser.readOntologyTermValidations().size());
		parser.clearOntologyTermValidations();
		assertEquals(0,parser.readOntologyTermValidations().size());
	}

}
