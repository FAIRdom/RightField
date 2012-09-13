package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PropertyValidationFormulaDefinitionTest {

	@Test
	public void testSeperator() {
		assertEquals("^",PropertyValidationForumlaDefinition.SEPERATOR);
	}
	
	@Test
	public void testEncoding() {
		
		assertEquals("propliteral^hidden",PropertyValidationForumlaDefinition.encode("hidden"));				
	}
	
	@Test
	public void testDecode() {
		String sheet = PropertyValidationForumlaDefinition.decode("propliteral^sheetA");
		assertEquals("sheetA",sheet);
		
		sheet = PropertyValidationForumlaDefinition.decode("AND(B2<>\"propliteral^sheetX\")");
		assertEquals("sheetX",sheet);
	}	
	
	@Test
	public void testValid() {
		assertTrue(PropertyValidationForumlaDefinition.valid("propliteral^sheetA"));
		assertFalse(PropertyValidationForumlaDefinition.valid("propliteral:sheetA"));
		assertFalse(PropertyValidationForumlaDefinition.valid("propertyliteral^sheetX"));
		assertFalse(PropertyValidationForumlaDefinition.valid("propertyliteral^"));
		assertFalse(PropertyValidationForumlaDefinition.valid("wksowlv0"));
		
		assertTrue(PropertyValidationForumlaDefinition.valid("AND(B2<>\"propliteral^wksowlv0\")"));
	}

}
