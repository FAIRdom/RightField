/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.impl.GeneralSheetTests;

public class SheetHSSFImplTest extends GeneralSheetTests {		
	
	@Test
	public void testGetValidationData() throws Exception {
		SheetHSSFImpl sheet = (SheetHSSFImpl)getTestSheet();
		List<HSSFDataValidation> validationData = sheet.getValidationData();
		assertEquals(1,validationData.size());
		HSSFDataValidation val = validationData.get(0);
		CellRangeAddress[] cellRangeAddresses = val.getRegions().getCellRangeAddresses();
		assertEquals(1,cellRangeAddresses.length);
		CellRangeAddress rangeAddresses = cellRangeAddresses[0];
		assertEquals(4,rangeAddresses.getFirstColumn());
		assertEquals(4,rangeAddresses.getLastColumn());
		assertEquals(11,rangeAddresses.getFirstRow());
		assertEquals(11,rangeAddresses.getLastRow());
	}
		
	public Sheet getTestSheetWithProperties() throws Exception {
		return SpreadsheetTestHelper.getWorkbookSheetHSSF(DocumentsCatalogue.bookWithPropertiesURI(),0);
	}
	
	
	protected Workbook getTestWorkbook() throws Exception {
		return SpreadsheetTestHelper.openWorkbookHSSF(DocumentsCatalogue.simpleAnnotatedworkbookURI());
	}
	
	protected Sheet getTestSheet() throws Exception {
		return SpreadsheetTestHelper.getWorkbookSheetHSSF(DocumentsCatalogue.simpleAnnotatedworkbookURI(),0);
	}
	
	protected Workbook getBlankWorkbook() throws Exception {
		return SpreadsheetTestHelper.getBlankWorkbook();
	}
	
	protected Sheet getBlankSheet() throws Exception {
		return SpreadsheetTestHelper.getBlankWorkbook().createSheet();
	}	
	
}
