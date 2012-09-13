package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.DummyWorkbookChangeListener;


public class WorkbookXSSFImplTest {

	@Test
	public void testVisibleSheets() throws Exception {
		WorkbookXSSFImpl book = getTestWorkbook();

		assertEquals(3,book.getSheets().size());
		assertEquals(1,book.getVisibleSheets().size());
		assertTrue(book.getVisibleSheets().contains(book.getSheet(0)));	
	}
	
	
	@Test
	public void testDeleteSheet() throws Exception {
		WorkbookXSSFImpl workbook = getTestWorkbook();
		assertEquals(3, workbook.getSheets().size());
		assertNotNull(workbook.getSheet("Sheet0"));
		workbook.deleteSheet("Sheet0");
		assertEquals(2, workbook.getSheets().size());
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
		WorkbookXSSFImpl book = getEmptyWorkbook();
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
		WorkbookXSSFImpl workbook = getEmptyWorkbook();		
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
		WorkbookXSSFImpl workbook = getEmptyWorkbook();
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
		WorkbookXSSFImpl workbook = getEmptyWorkbook();
		
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
		WorkbookXSSFImpl wb = getTestWorkbook();
		File f = SpreadsheetTestHelper.getTempFile("xlsx");
		Sheet sheet = wb.getSheet(0);
		sheet.getColumnWidth(3);
		assertFalse(f.exists());
		wb.saveAs(f.toURI());
		assertTrue(f.exists());
		assertNotNull(WorkbookFactory.create(new FileInputStream(f)));
		
		//this is to test a weird problem with the sheet column widths becoming corrupt after a save
		sheet = wb.getSheet(0);
		
		Cell cell = sheet.getCellAt(3, 11);
		assertEquals("Experimental Design",cell.getValue());
		sheet.getColumnWidth(3);
	}
	
	
	@Test
	public void testColumnWidthAfterSave() throws Exception {
		WorkbookXSSFImpl wb = getTestWorkbook(); //opens a workbook using WorkbookFactory
		File f = SpreadsheetTestHelper.getTempFile("xlsx");
		Sheet sheet = wb.getSheet(0);		
		Cell cell = sheet.getCellAt(3, 11);
		assertEquals("Experimental Design",cell.getValue());
		sheet.getColumnWidth(3); // <- works fine
		
		wb.saveAs(f.toURI());
		
		sheet = wb.getSheet(0);		
		cell = sheet.getCellAt(3, 11);
		assertEquals("Experimental Design",cell.getValue());
		sheet.getColumnWidth(3); //<- IndexOutOfBoundsException thrown
	}
	
	@Test
	public void testColumnWidthPOI() throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("hello world");
		assertEquals("hello world",workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
		assertEquals(2048,workbook.getSheetAt(0).getColumnWidth(0));
		
		//gets a UUID based temporary file
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));		
		String uuid = UUID.randomUUID().toString();
		File f = new File(tmpDir,uuid+".xlsx");
		
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));        
        workbook.write(stream);        
        stream.close();
        assertTrue(f.exists());
        
        assertEquals("hello world",workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
		assertEquals(2048,workbook.getSheetAt(0).getColumnWidth(0));        
	}

	
	private WorkbookXSSFImpl getEmptyWorkbook() throws Exception {
		return SpreadsheetTestHelper.getBlankXSSFWorkbook();
	}
	
	//opens the workbook src/test/resources/simple_annotated_book.xls
	private WorkbookXSSFImpl getTestWorkbook() throws Exception {
		return SpreadsheetTestHelper.openWorkbookXSSF(DocumentsCatalogue.simpleAnnotatedXLSXWorkbookURI());
	}
}
