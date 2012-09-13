package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;


public class CellXSSFImplTest {
	
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
