package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.awt.Font;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.impl.GeneralCellTests;

public class CellHSSFImplTest extends GeneralCellTests {		
	
	
	@Test
	public void getFont() throws Exception {
		Workbook workbook = getTestWorkbook();
		Cell cell = workbook.getSheet(0).addCellAt(1, 1);
		Font font = cell.getFont();
		assertEquals("Arial",font.getFamily());
		assertEquals(Font.PLAIN,font.getStyle());
		//test it is cached
		assertSame(font,cell.getFont());
		
		//try again with a new workbook, to test caching
		workbook = getTestWorkbook();
		cell = workbook.getSheet(0).addCellAt(1, 1);
		assertEquals("Arial",cell.getFont().getFamily());
		assertEquals(Font.PLAIN,cell.getFont().getStyle());
		assertNotSame(font,cell.getFont());
	}
	
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
