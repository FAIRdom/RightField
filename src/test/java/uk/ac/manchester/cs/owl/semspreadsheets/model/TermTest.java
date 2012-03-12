package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class TermTest {

	@Test
	public void testFormattedName() {
		Term term = new Term(IRI.create("http://term#name_with_underscores"), "name_with_underscores");
		assertEquals("name_with_underscores",term.getName());
		assertEquals("name with underscores",term.getFormattedName());		
	}

}
