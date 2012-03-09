package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;


public class WorkbookHSSFImplTest {

	@Test
	public void testVisibleSheets() throws Exception {
		WorkbookHSSFImpl book = getTestWorkbook();

		assertEquals(3,book.getSheets().size());
		assertEquals(1,book.getVisibleSheets().size());
		assertTrue(book.getVisibleSheets().contains(book.getSheet(0)));	
	}
	
	@Test
	public void testSheetNameExists() throws Exception {
		WorkbookHSSFImpl book = getEmptyWorkbook();
		assertTrue(book.sheetNameExists("Sheet0"));
		assertFalse(book.sheetNameExists("Sheet1"));
		Sheet sheet = book.addSheet();
		assertTrue(book.sheetNameExists("Sheet1"));
		sheet.setName("Frog");
		assertTrue(book.sheetNameExists("Frog"));
		assertFalse(book.sheetNameExists("Sheet1"));		
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
	public void testAddingVeryHiddenSheets() throws Exception {
		WorkbookHSSFImpl workbook = getEmptyWorkbook();		
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
		WorkbookHSSFImpl workbook = getEmptyWorkbook();
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
		WorkbookHSSFImpl workbook = getEmptyWorkbook();
		
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
	
	private WorkbookHSSFImpl getEmptyWorkbook() throws Exception {
		return SpreadsheetTestHelper.getBlankWorkbook();
	}
	
	//opens the workbook src/test/resources/simple_annotated_book.xls
	private WorkbookHSSFImpl getTestWorkbook() throws Exception {
		return SpreadsheetTestHelper.openWorkbookHSSF("simple_annotated_book.xls");
	}
	
	
}
