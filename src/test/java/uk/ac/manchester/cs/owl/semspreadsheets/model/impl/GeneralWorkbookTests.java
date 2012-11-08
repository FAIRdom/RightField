package uk.ac.manchester.cs.owl.semspreadsheets.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DummyWorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookFactory;
import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.WorkbookHSSFImpl;

public abstract class GeneralWorkbookTests {
	
	@Test
	public void testVisibleSheets() throws Exception {
		Workbook book = getTestWorkbook();

		assertEquals(4,book.getSheets().size());
		assertEquals(2,book.getVisibleSheets().size());
		assertTrue(book.getVisibleSheets().contains(book.getSheet(0)));	
	}	
	
	@Test
	public void testDeleteSheet() throws Exception {
		Workbook workbook = getTestWorkbook();
		assertEquals(4, workbook.getSheets().size());
		assertNotNull(workbook.getSheet("Sheet0"));
		workbook.deleteSheet("Sheet0");
		assertEquals(3, workbook.getSheets().size());
		assertNull(workbook.getSheet("Sheet0"));			
	}
	
	@Test
	public void testGetAllChangeListeners() throws Exception {
		Workbook book = getTestWorkbook();
		assertEquals(0,book.getAllChangeListeners().size());
		WorkbookChangeListener listener = new DummyWorkbookChangeListener();
		book.addChangeListener(listener);
		assertEquals(1,book.getAllChangeListeners().size());
		assert(book.getAllChangeListeners().contains(listener));
	}
	
	@Test
	public void testClearChangeListeners() throws Exception {
		Workbook book = getTestWorkbook();
		assertEquals(0,book.getAllChangeListeners().size());
		WorkbookChangeListener listener = new DummyWorkbookChangeListener();
		book.addChangeListener(listener);
		book.clearChangeListeners();
		assertEquals(0,book.getAllChangeListeners().size());
	}
	
	@Test
	public void testContainsSheet() throws Exception {
		Workbook book = getEmptyWorkbook();
		assertTrue(book.containsSheet("Sheet0"));
		assertFalse(book.containsSheet("Sheet1"));
		Sheet sheet = book.addSheet();
		assertTrue(book.containsSheet("Sheet1"));
		sheet.setName("Frog");
		assertTrue(book.containsSheet("Frog"));
		assertFalse(book.containsSheet("Sheet1"));		
	}
	
	@Test 
	public void testAddingVeryHiddenSheets() throws Exception {
		Workbook workbook = getEmptyWorkbook();		
		assertEquals(1,workbook.getSheets().size());
		Sheet sheet = workbook.getSheet(0);
		
		Sheet vhidden_sheet = workbook.addVeryHiddenSheet();
		assertEquals(2,workbook.getSheets().size());
		assertEquals(vhidden_sheet, workbook.getSheet(1));
		assertEquals(sheet, workbook.getSheet(0));
		
		assertEquals("Sheet0",sheet.getName());
		assertEquals("Sheet1",vhidden_sheet.getName());
		
		assertFalse(workbook.getSheet(0).isHidden());
		assertFalse(workbook.getSheet(0).isVeryHidden());
		
		assertFalse(workbook.getSheet(1).isHidden());
		assertTrue(workbook.getSheet(1).isVeryHidden());
	}
	
	@Test
	public void testSheetNamingAfterDeletion() throws Exception {
		Workbook workbook = getEmptyWorkbook();
		assertEquals("Sheet0",workbook.getSheet(0).getName());
		workbook.addSheet();
		assertEquals("Sheet1",workbook.getSheet(1).getName());
		workbook.deleteSheet("Sheet1");
		assertEquals("Sheet0",workbook.getSheet(0).getName());
		workbook.addSheet();
		assertEquals("Sheet0",workbook.getSheet(0).getName());
		assertEquals("Sheet1",workbook.getSheet(1).getName());
		
		workbook = getEmptyWorkbook();
		
		workbook.addSheet();
		
		workbook.deleteSheet("Sheet0");
		assertEquals("Sheet1",workbook.getSheet(0).getName());
		workbook.addSheet();
		assertEquals("Sheet1",workbook.getSheet(0).getName());
		assertEquals("Sheet0",workbook.getSheet(1).getName());
		
	}
	
	@Test
	public void testWeirdSheetOrderProblem() throws Exception {
		Workbook workbook = getEmptyWorkbook();
		
		//starts with 1 sheet
		assertEquals(1,workbook.getSheets().size());
		assertEquals("Sheet0",workbook.getSheet(0).getName());
		
		//add sheet 2
		workbook.addSheet();
		assertEquals("Sheet1",workbook.getSheet(1).getName());
		
		//delete the first sheet
		workbook.deleteSheet(workbook.getSheet(0).getName());
		assertEquals("Sheet1",workbook.getSheet(0).getName());
		
		//add a very hidden sheet
		workbook.addVeryHiddenSheet();		
		assertEquals("Sheet1",workbook.getSheet(0).getName());
		assertEquals("Sheet0",workbook.getSheet(1).getName());
		
		//i now expect the first sheet to be visible and the 2nd sheet very hidden
		Sheet sheet0=workbook.getSheet(0);
		Sheet sheet1=workbook.getSheet(1);
		
		assertFalse(sheet0.isVeryHidden());
		assertTrue(sheet1.isVeryHidden());
		
	}	
	
	@Test
	public void testSave() throws Exception {
		Workbook wb = getTestWorkbook();
		File f = SpreadsheetTestHelper.getTempFile(getExtension());
		Sheet sheet = wb.getSheet(0);
		
		assertFalse(f.exists());
		wb.saveAs(f.toURI());
		assertTrue(f.exists());
		wb = WorkbookFactory.createWorkbook(f.toURI());
		assertNotNull(wb);
		
		//this is to test a weird problem with the sheet column widths becoming corrupt after a save
		sheet = wb.getSheet(0);
		
		Cell cell = sheet.getCellAt(3, 11);
		assertEquals("Experimental Design",cell.getValue());
		
	}			
	
	@Test
	public void testColumnWidthAfterSave() throws Exception {
		Workbook wb = getTestWorkbook(); //opens a workbook using WorkbookFactory
		File f = SpreadsheetTestHelper.getTempFile(getExtension());
		Sheet sheet = wb.getSheet(0);		
		Cell cell = sheet.getCellAt(3, 11);
		assertEquals("Experimental Design",cell.getValue());
		sheet.getColumnWidth(3); // <- works fine
		
		wb.saveAs(f.toURI());
		
		Workbook wb2 = WorkbookFactory.createWorkbook(f.toURI());
		sheet = wb2.getSheet(0);		
		cell = sheet.getCellAt(3, 11);
		assertEquals("Experimental Design",cell.getValue());
		sheet.getColumnWidth(3);
		
		sheet = wb.getSheet(0);		
		cell = sheet.getCellAt(3, 11);
		assertEquals("Experimental Design",cell.getValue());
		sheet.getColumnWidth(3); //<- IndexOutOfBoundsException thrown
	}
	
	@Test
	public void testReadColumnWidthAferLoad() throws Exception {
		Workbook book = getTestWorkbook();
		int expectedWidth = 0;
		//for some reason the width in the converted spreadsheet is slightly less than the original xls workbook
		if (book instanceof WorkbookHSSFImpl) {
			expectedWidth = 66;
		}
		else {
			expectedWidth = 48;
		}
		assertEquals(expectedWidth,book.getSheet(0).getColumnWidth(0));
	}
	
	protected abstract Workbook getTestWorkbook() throws Exception;
	protected abstract Workbook getEmptyWorkbook() throws Exception;
	protected abstract String getExtension() throws Exception;

}
