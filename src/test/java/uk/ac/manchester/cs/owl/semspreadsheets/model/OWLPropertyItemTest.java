package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class OWLPropertyItemTest {

	@Test
	public void testEquals() {
		OWLPropertyItem item = new OWLPropertyItem(IRI.create("http://info/stuff#property"),OWLPropertyType.DATA_PROPERTY);
		OWLPropertyItem item2 = new OWLPropertyItem(IRI.create("http://info/stuff#property"),OWLPropertyType.DATA_PROPERTY);
		OWLPropertyItem item3 = new OWLPropertyItem(IRI.create("http://info/stuff#property"),OWLPropertyType.OBJECT_PROPERTY);
		OWLPropertyItem item4 = new OWLPropertyItem(IRI.create("http://info/stuff#property2"),OWLPropertyType.DATA_PROPERTY);
		
		assertEquals(item,item2);
		assertFalse(item.equals(item3));
		assertFalse(item.equals(item4));
		assertFalse(item.equals("a string"));
	}
	
	@Test
	public void testHashcode() {
		OWLPropertyItem item = new OWLPropertyItem(IRI.create("http://info/stuff#property"),OWLPropertyType.DATA_PROPERTY);
		OWLPropertyItem item2 = new OWLPropertyItem(IRI.create("http://info/stuff#property"),OWLPropertyType.DATA_PROPERTY);
		OWLPropertyItem item3 = new OWLPropertyItem(IRI.create("http://info/stuff#property"),OWLPropertyType.OBJECT_PROPERTY);
		OWLPropertyItem item4 = new OWLPropertyItem(IRI.create("http://info/stuff#property2"),OWLPropertyType.DATA_PROPERTY);
		
		assertEquals(item.hashCode(),item2.hashCode());
		assertFalse(item.hashCode() == item3.hashCode());
		assertFalse(item.hashCode() == item4.hashCode());
	}

}
