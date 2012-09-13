/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Validation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.SheetHSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.WorkbookHSSFImpl;

public class SheetHSSFImplTest {
	
	@Test
	public void testGetTestSheet() throws Exception {
		//check that fetching of the sheet used in these tests is working correctly
		SheetHSSFImpl sheet = getTestSheet();
		assertNotNull(sheet);
		assertEquals("Sheet0",sheet.getName());
	}

	@Test
	public void testGetValidations() throws Exception {
		SheetHSSFImpl sheet = getTestSheet();
		Collection<Validation> validations = sheet.getValidations();
		List<Validation> list = new ArrayList<Validation>(validations);
		assertEquals(1,list.size());
		Validation val = list.get(0);
		assertEquals("wksowlv0",val.getFormula());
		assertEquals(4,val.getFirstColumn());
		assertEquals(4,val.getLastColumn());
		assertEquals(11,val.getFirstRow());
		assertEquals(11,val.getLastRow());
		assertEquals(sheet,val.getSheet());
		assertEquals("Sheet0!E12:E12",val.getRange().toString());
	}
	
	@Test
	public void testGetValidationData() throws Exception {
		SheetHSSFImpl sheet = getTestSheet();
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
	
	@Test
	public void testClearValidationData() throws Exception {
		SheetHSSFImpl sheet = getTestSheet();
		List<HSSFDataValidation> validationData = sheet.getValidationData();
		assertEquals(1,validationData.size());
		sheet.clearValidationData();
		validationData = sheet.getValidationData();
		assertEquals(0,validationData.size());		
	}
	
	@Test
	public void testGetContainingValidations() throws Exception {
		SheetHSSFImpl sheet = getTestSheet();
		
		Range r = new Range(sheet,4,11,4,11);
		Collection<Validation> validations = sheet.getContainingValidations(r);		
		List<Validation> list = new ArrayList<Validation>(validations);
		
		assertEquals(1,list.size());
		Validation val = list.get(0);
		assertEquals("wksowlv0",val.getFormula());
		assertEquals(sheet,val.getSheet());
		assertEquals("Sheet0!E12:E12",val.getRange().toString());		
		
		r = new Range(sheet,3,3,3,3);
		validations = sheet.getContainingValidations(r);		
		list = new ArrayList<Validation>(validations);
		
		assertEquals(0,list.size());		
	}
	
	@Test
	public void testGetIntersectingValidations() throws Exception {
		SheetHSSFImpl sheet = getTestSheet();
		
		Range r = new Range(sheet,3,10,5,12);
		Collection<Validation> validations = sheet.getIntersectingValidations(r);
		List<Validation> list = new ArrayList<Validation>(validations);
		assertEquals(1,list.size());
		Validation val = list.get(0);
		assertEquals("wksowlv0",val.getFormula());
		assertEquals(sheet,val.getSheet());
		assertEquals("Sheet0!E12:E12",val.getRange().toString());
		
		r = new Range(sheet,5,12,7,15);
		validations = sheet.getContainingValidations(r);		
		list = new ArrayList<Validation>(validations);
		
		assertEquals(0,list.size());	
	}
	
	@Test
	public void testAddPropertyValidation() throws Exception {		
		Sheet sheet = getTestWorkbook().addSheet();
		assertEquals(0,sheet.getValidations().size());
		sheet.addLiteralValidation("wksowlv0", 2, 3,3, 4);
		assertEquals(1,sheet.getValidations().size());
		Validation validation = sheet.getValidations().iterator().next();
		assertTrue(validation.isLiteralValidation());
		assertFalse(validation.isDataValidation());
		assertEquals("propliteral^wksowlv0",validation.getFormula());
		assertEquals(new Range(sheet,2,3,3,4),validation.getRange());
	}
	
	@Test
	public void testAddValidation() throws Exception {
		SheetHSSFImpl sheet = (SheetHSSFImpl) getTestWorkbook().addSheet();
		
		Collection<Validation> validations = sheet.getValidations();
		List<Validation> list = new ArrayList<Validation>(validations);
		assertEquals(0,list.size());
		sheet.addValidation("wksowlv0", 1, 1, 2, 2);
		validations = sheet.getValidations();	
		list = new ArrayList<Validation>(validations);		
		assertEquals(1,list.size());
		Validation val = list.get(0);
		assertEquals("wksowlv0",val.getFormula());
		assertEquals("Sheet1!B2:C3",val.getRange().toString());
	}
	
	@Test
	public void testHiddenSheets() throws Exception {
		WorkbookHSSFImpl book = getTestWorkbook();
		assertEquals(3,book.getSheets().size());
		Sheet sheet = book.getSheet(1);
		assertEquals("HiddenSheet",sheet.getName());
		assertTrue(sheet.isHidden());
		assertFalse(sheet.isVeryHidden());
	}
	
	@Test
	public void testSetHiddenSheet() throws Exception {
		Sheet sheet = getTestSheet();
		assertFalse(sheet.isHidden());
		sheet.setHidden(true);
		assertTrue(sheet.isHidden());
	}
	
	@Test
	public void testSetVisible() throws Exception {
		Sheet sheet = getTestWorkbook().getSheet(1);
		assertTrue(sheet.isHidden());
		sheet.setHidden(false);
		assertFalse(sheet.isHidden());
	}
	
	@Test
	public void testSetVeryHiddden() throws Exception {
		Sheet sheet = getTestSheet();
		assertFalse(sheet.isVeryHidden());
		assertFalse(sheet.isHidden());
		sheet.setVeryHidden(true);
		assertTrue(sheet.isVeryHidden());
		assertFalse(sheet.isHidden());
	}
	
	@Test
	public void testVeryHiddenSheets() throws Exception {
		WorkbookHSSFImpl book = getTestWorkbook();
		Sheet sheet = book.getSheet(2);
		assertEquals("wksowlv0",sheet.getName());
		assertFalse(sheet.isHidden());
		assertTrue(sheet.isVeryHidden());		
	}
	
	@Test
	public void testVisibleSheets() throws Exception {
		WorkbookHSSFImpl book = getTestWorkbook();
		Sheet sheet = book.getSheet(0);
		assertEquals("Sheet0",sheet.getName());
		assertFalse(sheet.isHidden());
		assertFalse(sheet.isVeryHidden());		
	}
	
	@Test
	public void testIndex() throws Exception {
		Workbook wb = SpreadsheetTestHelper.getBlankWorkbook();
		Sheet sheet = wb.getSheet(0);
		assertEquals(0,sheet.getIndex());
		sheet = wb.addSheet();
		assertEquals(1,sheet.getIndex());
	}
	
	//opens the workbook src/test/resources/simple_annotated_book.xls
	private WorkbookHSSFImpl getTestWorkbook() throws Exception {
		return SpreadsheetTestHelper.openWorkbookHSSF(DocumentsCatalogue.simpleAnnotatedworkbookURI());
	}
	
	//opens the first sheet from test workbook src/test/resources/simple_annotated_sheet.xls used for most of these tests
	private SheetHSSFImpl getTestSheet() throws Exception {
		return SpreadsheetTestHelper.getWorkbookSheet(DocumentsCatalogue.simpleAnnotatedworkbookURI(),0);
	}
	
}
