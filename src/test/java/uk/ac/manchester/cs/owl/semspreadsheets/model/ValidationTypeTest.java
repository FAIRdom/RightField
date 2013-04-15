package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class ValidationTypeTest {

	@Test
	public void testGetValuesForNoOntologies() {
		ValidationType[] valuesNoOntologies = ValidationType
				.valuesNoOntologies();
		assertEquals(1, valuesNoOntologies.length);
		assertTrue(Arrays.asList(valuesNoOntologies).contains(
				ValidationType.FREETEXT));
	}

	@Test
	public void testGetValidationTypeSKOS() {
		ValidationType[] valuesForSKOS = ValidationType.valuesForSKOS();
		assertEquals(3, valuesForSKOS.length);

		// the order is important
		ValidationType[] expected = new ValidationType[] {
				ValidationType.FREETEXT, ValidationType.NARROWER,
				ValidationType.DIRECTNARROWER };
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], valuesForSKOS[i]);
		}

	}

	@Test
	public void testGetValidationTypeOWL() {
		ValidationType[] valuesForOWL = ValidationType.valuesForOWL();
		assertEquals(5, valuesForOWL.length);
		// the order is important
		ValidationType[] expected = new ValidationType[] {
				ValidationType.FREETEXT, ValidationType.SUBCLASSES,
				ValidationType.DIRECTSUBCLASSES, ValidationType.INDIVIDUALS,
				ValidationType.DIRECTINDIVIDUALS };
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], valuesForOWL[i]);
		}
	}
}
