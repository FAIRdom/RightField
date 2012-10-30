package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.WorkbookHSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl.WorkbookXSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFormat;

public class WorkbookFactoryTest {
	
	@Test
	public void testCreateWorkbook() {
		Workbook book = WorkbookFactory.createWorkbook();
		assertTrue(book instanceof WorkbookHSSFImpl);
		assertEquals(1,book.getSheets().size());
	}
	
	@Test
	public void testCreateWorkbookbyFormat() {
		Workbook book = WorkbookFactory.createWorkbook(WorkbookFormat.EXCEL97);
		assertTrue(book instanceof WorkbookHSSFImpl);
		assertEquals(1,book.getSheets().size());
		
		book = WorkbookFactory.createWorkbook(WorkbookFormat.EXCEL2007);
		assertTrue(book instanceof WorkbookXSSFImpl);
		assertEquals(1,book.getSheets().size());
	}
	
	@Test
	public void testLoadXLSWorkbook() throws Exception {
		Workbook book = WorkbookFactory.createWorkbook(DocumentsCatalogue.populatedJermWorkbookURI());
		assertTrue(book instanceof WorkbookHSSFImpl);
		assertEquals(11,book.getSheets().size());
		assertEquals("Metadata Template",book.getSheet(0).getName());
	}
	
	@Test
	public void testLoadXLSXWorkbook() throws Exception {
		Workbook book = WorkbookFactory.createWorkbook(DocumentsCatalogue.populatedJermWorkbookXLSXURI());
		assertTrue(book instanceof WorkbookXSSFImpl);
		assertEquals(11,book.getSheets().size());
		assertEquals("Metadata Template",book.getSheet(0).getName());
	}

}
