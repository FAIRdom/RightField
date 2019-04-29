package uk.org.rightfield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RightFieldOptionsTest {

	@Test
	public void testExportArgs() {
		String [] args = new String[] {"-export","-id","fish","-format","rdf","c:\\file.xls"};
		RightFieldOptions opts = new RightFieldOptions(args);
		assertEquals(6,opts.count());
		assertEquals("rdf",opts.getExportFormat());
		assertTrue(opts.isExport());
		assertEquals("fish",opts.getId());
		assertEquals("c:\\file.xls",opts.getFilename());
		assertNull(opts.getProperty());
	}
	
	@Test
	public void testExportArgsDefaultFormat() {
		String [] args = new String[] {"-export","-id","fish","c:\\file.xls"};
		RightFieldOptions opts = new RightFieldOptions(args);
		assertEquals(4,opts.count());
		assertTrue(opts.isExport());
		assertEquals("rdf",opts.getExportFormat());
		assertEquals("fish",opts.getId());
		assertEquals("c:\\file.xls",opts.getFilename());
		assertNull(opts.getProperty());
	}
	
	@Test
	public void testExportCSVFormat() {
		String [] args = new String[] {"-export","-id","fish","-format","csv","c:\\file.xls"};
		RightFieldOptions opts = new RightFieldOptions(args);
		assertEquals(6,opts.count());
		assertTrue(opts.isExport());
		assertEquals("csv",opts.getExportFormat());
		assertEquals("fish",opts.getId());
		assertEquals("c:\\file.xls",opts.getFilename());
		assertNull(opts.getProperty());
	}
	
	@Test
	public void testWithProperty() {
		String [] args = new String[] {"-export","-property","http://ontology.org#property","-id","fish","c:\\file.xls"};
		RightFieldOptions opts = new RightFieldOptions(args);
		assertEquals(6,opts.count());
		assertTrue(opts.isExport());
		assertEquals("rdf",opts.getExportFormat());
		assertEquals("fish",opts.getId());
		assertEquals("c:\\file.xls",opts.getFilename());
		assertEquals("http://ontology.org#property",opts.getProperty());
	}

}
