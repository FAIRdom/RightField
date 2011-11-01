package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class WorkbookHSSFImplTest {

	@Test
	public void testVisibleSheets() throws Exception {
		WorkbookHSSFImpl book = getTestWorkbook();

		assertEquals(3,book.getSheets().size());
		assertEquals(1,book.getVisibleSheets().size());
		assertTrue(book.getVisibleSheets().contains(book.getSheet(0)));	
	}
	
	//opens the workbook src/test/resources/simple_annotated_book.xls
	private WorkbookHSSFImpl getTestWorkbook() throws Exception {
		return SpreadsheetTestHelper.openWorkbookHSSF("simple_annotated_book.xls");
	}
}
