package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.impl.GeneralSheetTests;

public class SheetXSSFImplTest extends GeneralSheetTests {
	
	@Test
	public void testGetValidationData() throws Exception {
		SheetXSSFImpl sheet = (SheetXSSFImpl)getTestSheet();
		List<XSSFDataValidation> validationData = sheet.getValidationData();
		assertEquals(1,validationData.size());
		XSSFDataValidation val = validationData.get(0);
		CellRangeAddress[] cellRangeAddresses = val.getRegions().getCellRangeAddresses();
		assertEquals(1,cellRangeAddresses.length);
		CellRangeAddress rangeAddresses = cellRangeAddresses[0];
		assertEquals(4,rangeAddresses.getFirstColumn());
		assertEquals(4,rangeAddresses.getLastColumn());
		assertEquals(11,rangeAddresses.getFirstRow());
		assertEquals(11,rangeAddresses.getLastRow());
	}
		
	
	@Test
	public void testGettingValidationsAfterAddingCustomInPOI() throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();
		List<XSSFDataValidation> dataValidations = sheet.getDataValidations();	//<-- works
		assertEquals(0, dataValidations.size());
		
		//create the cell that will have the validation applied
		sheet.createRow(0).createCell(0);		
		
		DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
		DataValidationConstraint constraint = dataValidationHelper.createCustomConstraint("SUM($A$1:$A$1) <= 3500");
		CellRangeAddressList addressList = new CellRangeAddressList(0, 0, 0, 0);
		DataValidation validation = dataValidationHelper.createValidation(constraint, addressList);
		sheet.addValidationData(validation);					
        		
		dataValidations = sheet.getDataValidations();	//<-- raised XmlValueOutOfRangeException	
		assertEquals(1, dataValidations.size());
	}
	
	
	//opens the workbook src/test/resources/simple_annotated_book.xls
	protected Workbook getTestWorkbook() throws Exception {
		return SpreadsheetTestHelper.openWorkbookXSSF(DocumentsCatalogue.simpleAnnotatedXLSXWorkbookURI());
	}
	
	//opens the first sheet from test workbook src/test/resources/simple_annotated_sheet.xls used for most of these tests
	protected Sheet getTestSheet() throws Exception {
		return (SheetXSSFImpl)getTestWorkbook().getSheet(0);
	}
	
	protected Workbook getBlankWorkbook() throws Exception {
		return SpreadsheetTestHelper.getBlankXSSFWorkbook();
	}
	
	protected Sheet getBlankSheet() throws Exception {
		return SpreadsheetTestHelper.getBlankXSSFWorkbook().createSheet();
	}


	@Override
	protected Sheet getTestSheetWithProperties() throws Exception {
		return SpreadsheetTestHelper.getWorkbookSheetXSSF(DocumentsCatalogue.bookWithPropertiesXLSXURI(), 0);
	}
	
}
