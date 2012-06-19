package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.impl.ValidationImpl;

public class PropertyFormulaEncoderTest {

	@Test
	public void testSeperator() {
		assertEquals("^",PropertyFormulaEncoder.SEPERATOR);
	}
	
	@Test
	public void testEncoding() {
		OWLPropertyItem item = new OWLPropertyItem(IRI.create("http://owl/ontology#Thing"),OWLPropertyType.OBJECT_PROPERTY);
		assertEquals("property^hidden^<http://owl/ontology#Thing>^OBJECT_PROPERTY",PropertyFormulaEncoder.encode("hidden",item));
		
		item = new OWLPropertyItem(IRI.create("http://owl/ontology#Thing"),OWLPropertyType.DATA_PROPERTY);
		assertEquals("property^hidden^<http://owl/ontology#Thing>^DATA_PROPERTY",PropertyFormulaEncoder.encode("hidden",item));
	}
	
	@Test
	public void testDecode() {
		OWLPropertyItem item = PropertyFormulaEncoder.decode("property^hidden^<http://owl/ontology#Thing>^DATA_PROPERTY");
		assertEquals(IRI.create("http://owl/ontology#Thing"),item.getIRI());
		assertEquals(OWLPropertyType.DATA_PROPERTY,item.getPropertyType());
		
		item = PropertyFormulaEncoder.decode("property^hidden^<http://owl/ontology#Thing>^OBJECT_PROPERTY");
		assertEquals(IRI.create("http://owl/ontology#Thing"),item.getIRI());
		assertEquals(OWLPropertyType.OBJECT_PROPERTY,item.getPropertyType());
	}
	
	@Test
	public void testConstructFromValidation() {
		WorkbookManager manager = new WorkbookManager();
		Sheet sheet = manager.getWorkbook().getSheet(0);
		
		Validation v = new ValidationImpl("property^wksowlv0^<http://www.mygrid.org.uk/ontology/JERMOntology#hasCharacteristics>^DATA_PROPERTY",sheet,1,1,1,1);
		OntologyTermValidation termValidation = PropertyFormulaEncoder.constructFromValidation(v, manager.getOntologyManager());
		assertEquals(sheet, termValidation.getRange().getSheet());
		assertEquals(new Range(sheet,1,1,1,1),termValidation.getRange());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#hasCharacteristics",termValidation.getValidationDescriptor().getOWLPropertyItem().getIRI().toString());
		assertEquals(OWLPropertyType.DATA_PROPERTY,termValidation.getValidationDescriptor().getOWLPropertyItem().getPropertyType());
		
	}

}
