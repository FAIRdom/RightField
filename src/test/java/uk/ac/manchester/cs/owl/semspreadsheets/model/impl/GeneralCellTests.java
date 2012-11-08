package uk.ac.manchester.cs.owl.semspreadsheets.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.CellHSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl.CellXSSFImpl;

public abstract class GeneralCellTests {
	
	@Test
	public void testReusesStyleForFillColour() throws Exception {
		Workbook workbook = getTestWorkbook();
		Cell cellA = workbook.getSheet(0).addCellAt(1, 1);
		Cell cellB = workbook.getSheet(0).addCellAt(2, 1);
		cellA.setBackgroundFill(Color.BLUE);
		cellB.setBackgroundFill(Color.YELLOW);		
		cellB.setBackgroundFill(Color.BLUE);
		if (cellA instanceof CellXSSFImpl) {
			assertEquals(((CellXSSFImpl)cellA).getInnards().getCellStyle(), ((CellXSSFImpl)cellB).getInnards().getCellStyle());
		}
		else {			
			assertEquals(((CellHSSFImpl)cellA).getInnards().getCellStyle(), ((CellHSSFImpl)cellB).getInnards().getCellStyle());
		}
	}
	
	@Test
	public void testGetSheetName() throws Exception {
		Cell cell = getTestCell2();
		assertEquals("Sheet1",cell.getSheetName());
	}
	
	@Test
	public void testGetSheetIndex() throws Exception {
		Cell cell = getTestCell2();
		assertEquals(3,cell.getSheetIndex());
	}

	@Test
	public void testEquals() throws Exception {
		Workbook workbook = getTestWorkbook();
		Workbook workbook2 = getTestWorkbook();
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
		Workbook workbook = getTestWorkbook();
		Workbook workbook2 = getTestWorkbook();
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
	public void setBackgroundFill() throws Exception {
		Workbook workbook = getTestWorkbook();
		Cell cell = workbook.getSheet(0).addCellAt(1, 1);
		cell.setBackgroundFill(Color.BLUE);
		cell = workbook.getSheet(0).getCellAt(1, 1);
		assertEquals(Color.BLUE, cell.getBackgroundFill());
	}
	
	@Test
	public void testForegroundColour() throws Exception {
		Cell cell = getTestCell();				
		Color col = cell.getForeground();
		assertEquals(Color.RED,col);
	}
	@Test
	public void testGetValue() throws Exception {
		Cell cell = getTestCell();
		assertEquals("hello",cell.getValue());
	}
	
	@Test
	public void testSetValue() throws Exception {
		Cell cell = getTestCell();
		cell.setValue("Hello World");
		assertEquals("Hello World",cell.getValue());
	}
	
	protected abstract Cell getTestCell() throws Exception;
	protected abstract Cell getTestCell2() throws Exception;
	protected abstract Workbook getTestWorkbook() throws Exception;

}
