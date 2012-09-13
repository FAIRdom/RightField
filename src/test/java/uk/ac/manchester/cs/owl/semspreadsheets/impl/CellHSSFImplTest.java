package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;

public class CellHSSFImplTest {
	
	private Workbook workbook;
	private Workbook workbook2;
	@Before
	public void setup() throws Exception {
		workbook = SpreadsheetTestHelper.getBlankWorkbook();
		workbook.addSheet();
		workbook2 = SpreadsheetTestHelper.getBlankWorkbook();		
	}
	
	@Test
	public void testReusesStyleForFillColour() throws Exception {
		CellHSSFImpl cellA = (CellHSSFImpl)workbook.getSheet(0).addCellAt(1, 1);
		CellHSSFImpl cellB = (CellHSSFImpl)workbook.getSheet(0).addCellAt(2, 1);
		cellA.setBackgroundFill(Color.BLUE);
		cellB.setBackgroundFill(Color.YELLOW);		
		cellB.setBackgroundFill(Color.BLUE);
		assertEquals(cellA.getInnards().getCellStyle(), cellB.getInnards().getCellStyle());
	}

	@Test
	public void testEquals() throws Exception {
		Cell cellA = workbook.getSheet(0).addCellAt(1,1);
		Cell cellA1 = workbook.getSheet(0).addCellAt(1,1);
		Cell cellB = workbook.getSheet(0).addCellAt(1,2);
		Cell cellC = workbook.getSheet(1).addCellAt(1, 1);
		Cell cellD = workbook2.getSheet(0).addCellAt(1, 1);
		
		assertTrue(cellA.equals(cellA));
		assertTrue(cellA.equals(cellA1));
		assertTrue(cellA1.equals(cellA));
		assertFalse(cellA.equals(cellB));
		assertFalse(cellA.equals(cellC));
		assertFalse(cellC.equals(cellB));
		
		assertFalse(cellA.equals(cellD));
		assertFalse(cellD.equals(cellA));
	}
	
	@Test
	public void testHashCode() throws Exception {
		Cell cellA = workbook.getSheet(0).addCellAt(1,1);
		Cell cellA1 = workbook.getSheet(0).addCellAt(1,1);
		Cell cellB = workbook.getSheet(0).addCellAt(1,2);
		Cell cellC = workbook.getSheet(1).addCellAt(1, 1);
		Cell cellD = workbook2.getSheet(0).addCellAt(1, 1);
		
		assertEquals(cellA.hashCode(),cellA1.hashCode());
		assertFalse(cellA.hashCode()==cellB.hashCode());
		assertFalse(cellA.hashCode()==cellC.hashCode());
		assertFalse(cellA.hashCode()==cellD.hashCode());		
	}

}
