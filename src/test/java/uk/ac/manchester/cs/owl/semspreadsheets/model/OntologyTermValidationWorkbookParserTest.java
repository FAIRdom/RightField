package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;
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
		Sheet sheet = manager.getWorkbook().getSheet(0);
		assertNotNull(sheet);
		assertEquals("Sheet0",sheet.getName());
		OWLPropertyItem property = new OWLPropertyItem(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#hasCharacteristics"),OWLPropertyType.OBJECT_PROPERTY);		
		assertNotNull(property);
		
		Cell cell = sheet.addCellAt(1, 1);
		assertNotNull(cell);
		Range range = new Range(sheet, cell);
		
		OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(property, ontManager);
		OntologyTermValidation validation = new OntologyTermValidation(descriptor, range);
		
		
		assertEquals(1,manager.getWorkbook().getSheets().size());
		assertEquals(1,manager.getWorkbook().getVisibleSheets().size());
		ArrayList<OntologyTermValidation> validations = new ArrayList<OntologyTermValidation>();
		validations.add(validation);
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
		assertEquals(sheet,v.getSheet());
		assertEquals(new Range(sheet,cell),v.getRange());
		assertEquals("wksowlv0:<http://www.mygrid.org.uk/ontology/JERMOntology#hasCharacteristics>",v.getListName());
		
	}
	
	@Test
	public void testWritingDataValidation() throws Exception {
		Sheet sheet = manager.getWorkbook().getSheet(0);
		assertNotNull(sheet);
		assertEquals("Sheet0",sheet.getName());
		OWLPropertyItem property = ontManager.getOWLObjectProperties().iterator().next();
		assertNotNull(property);
		
		Cell cell = sheet.addCellAt(0, 0);
		assertNotNull(cell);
		Range range = new Range(sheet, cell);		
		
		OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(ValidationType.INDIVIDUALS,IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#Project"),null,ontManager);
		OntologyTermValidation validation = new OntologyTermValidation(descriptor, range);
		
		
		assertEquals(1,manager.getWorkbook().getSheets().size());
		assertEquals(1,manager.getWorkbook().getVisibleSheets().size());
		ArrayList<OntologyTermValidation> validations = new ArrayList<OntologyTermValidation>();
		validations.add(validation);
		
		Collection<Validation> sheetValidations = manager.getWorkbook().getSheet(0).getValidations();
		assertEquals(0,sheetValidations.size());
		
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
		assertEquals(sheet,v.getSheet());
		assertEquals(new Range(sheet,cell),v.getRange());
		assertEquals("wksowlv0",v.getListName());
	}

}
