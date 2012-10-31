package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
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
