package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;


public class CellXSSFImplTest {
	
	private Workbook workbook;
	private Workbook workbook2;
	@Before
	public void setup() throws Exception {
		workbook = SpreadsheetTestHelper.getBlankXSSFWorkbook();
		workbook.addSheet();
		workbook2 = SpreadsheetTestHelper.getBlankXSSFWorkbook();		
	}
	
	@Test
	public void testReusesStyleForFillColour() throws Exception {
		CellXSSFImpl cellA = (CellXSSFImpl)workbook.getSheet(0).addCellAt(1, 1);
		CellXSSFImpl cellB = (CellXSSFImpl)workbook.getSheet(0).addCellAt(2, 1);
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
	
	@Test
	public void testBackgroundColour() throws Exception {
		CellXSSFImpl cell = getTestCell();				
		Color col = cell.getBackgroundFill();
		assertEquals(Color.BLUE,col);
	}
	
	@Test
	public void testForegroundColour() throws Exception {
		CellXSSFImpl cell = getTestCell();				
		Color col = cell.getForeground();
		assertEquals(Color.RED,col);
	}
	@Test
	public void testGetValue() throws Exception {
		CellXSSFImpl cell = getTestCell();
		assertEquals("hello",cell.getValue());
	}
	
	@Test
	public void testSetValue() throws Exception {
		CellXSSFImpl cell = getTestCell();
		cell.setValue("Hello World");
		assertEquals("Hello World",cell.getValue());
	}
	
	private CellXSSFImpl getTestCell() throws Exception {
		return (CellXSSFImpl)SpreadsheetTestHelper.openWorkbookXSSF(DocumentsCatalogue.workbookWithColoursXLSXURI()).getSheet(0).getCellAt(0, 0);
	}

}
