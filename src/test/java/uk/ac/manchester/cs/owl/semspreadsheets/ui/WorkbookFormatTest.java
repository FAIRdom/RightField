package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WorkbookFormatTest {
	
	@Test
	public void testFormats() throws Exception {
		WorkbookFormat [] formats = WorkbookFormat.getFormats();
		assertEquals(2,formats.length);
		assertEquals(WorkbookFormat.EXCEL97,formats[0]);
		assertEquals(WorkbookFormat.EXCEL2007,formats[1]);		
	}
	
	@Test
	public void testLabels() {
		assertEquals("Excel 97-2003 (*.xls)",WorkbookFormat.EXCEL97.toString());
		assertEquals("Excel 2007+ (*.xlsx)",WorkbookFormat.EXCEL2007.toString());
	}

}
