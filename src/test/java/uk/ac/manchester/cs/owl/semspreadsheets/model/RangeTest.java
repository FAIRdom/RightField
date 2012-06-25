package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.impl.SheetHSSFImpl;

public class RangeTest {
	
	private SheetHSSFImpl sheet;

	@Before
	public void setup() throws Exception {
		sheet = SpreadsheetTestHelper.getWorkbookSheet(DocumentsCatalogue.simpleWorkbookURI(),0);
	}

	@Test
	public void testGetCells() {
		//A3:B4
		Range range = new Range(sheet,0,2,1,3);
		assertEquals("A3:B4",range.getColumnRowAddress());
		
		Collection<Cell> cells = range.getCells();
		assertEquals(4,cells.size());
		String [] vals = new String [] {"a3","b3","a4","b4"};
		int x=0;
		for (Cell cell : cells) {
			assertEquals(vals[x],cell.getValue());		
			x++;
		}
		assertEquals(0,cells.iterator().next().getColumn());
		assertEquals(2,cells.iterator().next().getRow());
		
		Cell cell = sheet.getCellAt(4,3);
		range = new Range(sheet,cell);
		cells = range.getCells();
		assertEquals(1,cells.size());
		assertEquals(4,cells.iterator().next().getColumn());
		assertEquals(3,cells.iterator().next().getRow());
		assertEquals("e4",cells.iterator().next().getValue());		
	}
	
	@Test
	public void testColumnRowAddress() {
		Range range = new Range(sheet,1,2,4,3);
		assertEquals("B3:E4",range.getColumnRowAddress());
		
		Cell cell = sheet.getCellAt(2,2);
		range = new Range(sheet,cell);
		assertEquals("C3:C3",range.getColumnRowAddress());		
	}

}
