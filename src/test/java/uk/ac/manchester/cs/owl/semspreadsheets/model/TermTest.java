/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class TermTest {

	@Test
	public void testFormattedName() {
		Term term = new Term(IRI.create("http://term#name_with_underscores"), "name_with_underscores");
		assertEquals("name_with_underscores",term.getName());
		assertEquals("name with underscores",term.getFormattedName());
		
		term = new Term(IRI.create("http://term#name_prefix"), "prov:Person");
		assertEquals("prov:Person",term.getName());
		assertEquals("Person",term.getFormattedName());
		
		term = new Term(IRI.create("http://term#name_prefix_underscore_combined"), "prov:proper_Person");
		assertEquals("prov:proper_Person",term.getName());
		assertEquals("proper Person",term.getFormattedName());
	}

}
