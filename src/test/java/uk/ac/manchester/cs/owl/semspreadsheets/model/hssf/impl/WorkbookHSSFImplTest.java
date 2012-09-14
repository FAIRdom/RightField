/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.impl.GeneralWorkbookTests;


public class WorkbookHSSFImplTest extends GeneralWorkbookTests {
	
	@Test
	public void testColumnWidthPOI() throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
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
	
	
	protected Workbook getEmptyWorkbook() throws Exception {
		return SpreadsheetTestHelper.getBlankWorkbook();
	}
	
	//opens the workbook src/test/resources/simple_annotated_book.xls
	protected Workbook getTestWorkbook() throws Exception {
		return SpreadsheetTestHelper.openWorkbookHSSF(DocumentsCatalogue.simpleAnnotatedworkbookURI());
	}
	
	protected String getExtension() {
		return "xls";
	}
	
	
}
