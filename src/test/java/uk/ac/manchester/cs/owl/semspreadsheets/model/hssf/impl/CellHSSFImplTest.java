package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.impl.GeneralCellTests;

public class CellHSSFImplTest extends GeneralCellTests {			
	
	protected Workbook getTestWorkbook() throws Exception {
		Workbook workbook = SpreadsheetTestHelper.getBlankWorkbook();
		workbook.addSheet();
		return workbook;
	}
			
	protected Cell getTestCell() throws Exception {
		return SpreadsheetTestHelper.openWorkbookHSSF(DocumentsCatalogue.workbookWithColoursURI()).getSheet(0).getCellAt(0, 0);
	}
	
	protected Cell getTestCell2() throws Exception {
		return SpreadsheetTestHelper.openWorkbookHSSF(DocumentsCatalogue.simpleAnnotatedworkbookURI()).getSheet(3).getCellAt(4,5);
	}

}
