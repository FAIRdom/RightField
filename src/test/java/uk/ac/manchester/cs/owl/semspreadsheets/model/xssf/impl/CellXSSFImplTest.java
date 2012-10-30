package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.impl.GeneralCellTests;


public class CellXSSFImplTest extends GeneralCellTests {
	
	protected Workbook getTestWorkbook() throws Exception {
		Workbook workbook = SpreadsheetTestHelper.getBlankXSSFWorkbook();
		workbook.addSheet();
		return workbook;
	}
			
	protected Cell getTestCell() throws Exception {
		return SpreadsheetTestHelper.openWorkbookXSSF(DocumentsCatalogue.workbookWithColoursXLSXURI()).getSheet(0).getCellAt(0, 0);
	}
	
	protected Cell getTestCell2() throws Exception {
		return SpreadsheetTestHelper.openWorkbookXSSF(DocumentsCatalogue.populatedJermWorkbookXLSXURI()).getSheet(0).getCellAt(0, 0);
	}

}
