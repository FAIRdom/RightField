package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidationEntityTypeTest {
	
	@Test
	public void testEquals() {
		assertTrue(ValidationEntityType.CLASS.equals(ValidationEntityType.CLASS));
		assertFalse(ValidationEntityType.SKOS_CONCEPT.equals(ValidationEntityType.CLASS));
	}

}
