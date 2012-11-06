package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.values.XmlValueDisconnectedException;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.InvalidWorkbookFormatException;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookFactory;
import uk.ac.manchester.cs.owl.semspreadsheets.model.impl.GeneralWorkbookTests;


public class WorkbookXSSFImplTest extends GeneralWorkbookTests {
		
	@Test
	@Ignore("No longer needed as we found a workaround, but kept test to check if its fixed in POI in the future")
	public void testColumnWidthPOI() throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("hello world");
		workbook.getSheetAt(0).getColumnHelper().getColumn(0, false);
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
        
        workbook.getSheetAt(0).getColumnHelper().getColumn(0, false);
		assertEquals(2048,workbook.getSheetAt(0).getColumnWidth(0));        
	}
	
	@Test
	public void testSaveWorkbookTwice() throws Exception {
		//there was a particular problem with XSSF where after the 2nd save the workbook became corrupted due to bug https://issues.apache.org/bugzilla/show_bug.cgi?id=52233
		//this test was originally to test a workaround, but the workaround has changed and is now here to spot flag if/when Apache POI is fixed.
		Workbook wb = getTestWorkbook();
		assertEquals(1,wb.getSheet(0).getValidations().size());
		File f = SpreadsheetTestHelper.getTempFile(getExtension());			
		
		assertFalse(f.exists());
		wb.saveAs(f.toURI());
		assertTrue(f.exists());
		try {
			wb.saveAs(f.toURI());
			fail("GOOD News - its possible that this issue has been fixed by Apache POI");
		}
		catch(XmlValueDisconnectedException e) {
			//expected due to bug
		}
		try {
			wb = WorkbookFactory.createWorkbook(f.toURI());
			fail("GOOD News - its possible that this issue has been fixed by Apache POI");
		}
		catch(InvalidWorkbookFormatException e) {
			//expected due to bug
		}
		assertNotNull(wb);
		
		//this is to test a weird problem with the sheet column widths becoming corrupt after a save
		Sheet sheet = wb.getSheet(0);
		
		Cell cell = sheet.getCellAt(3, 11);
		assertEquals("Experimental Design",cell.getValue());		
		assertEquals(1,wb.getSheet(0).getValidations().size());
	}
	
	@Test
	public void testSaveWorkbookTwice2() throws Exception {
		// related to testSaveWorkbookTwice, but a slightly different error due
		// to the content (but probably the same root cause)
		// this test was originally to test a workaround, but the workaround has
		// changed and is now here to spot flag if/when Apache POI is fixed.
		Workbook wb = getTestWorkbook();
		Sheet sheet = wb.getSheet(0);
		Cell cell = sheet.addCellAt(0, 0);
		cell.setValue("Fred");
		File f = SpreadsheetTestHelper.getTempFile(getExtension());

		assertFalse(f.exists());
		wb.saveAs(f.toURI());
		assertTrue(f.exists());
		try {
			wb.saveAs(f.toURI());
			fail("GOOD News - its possible that this issue has been fixed by Apache POI");
		} catch (XmlValueDisconnectedException e) {
			//expected due to bug
		}

		try {
			assertNotNull(WorkbookFactory.createWorkbook(f.toURI()));
			fail("GOOD News - its possible that this issue has been fixed by Apache POI");
		} catch (InvalidWorkbookFormatException e) {
			// expected due to bug
		}

		// this is to test a weird problem with the sheet column widths becoming
		// corrupt after a save
		sheet = wb.getSheet(0);

		cell = sheet.getCellAt(0, 0);
		assertEquals("Fred", cell.getValue());
	}
		
	
	protected Workbook getEmptyWorkbook() throws Exception {
		return SpreadsheetTestHelper.getBlankXSSFWorkbook();
	}
	
	//opens the workbook src/test/resources/simple_annotated_book.xls
	protected Workbook getTestWorkbook() throws Exception {
		return SpreadsheetTestHelper.openWorkbookXSSF(DocumentsCatalogue.simpleAnnotatedXLSXWorkbookURI());
	}
	
	protected String getExtension() {
		return "xlsx";
	}
}
