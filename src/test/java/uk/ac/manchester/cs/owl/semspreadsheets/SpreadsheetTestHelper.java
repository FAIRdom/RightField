/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets;

import java.net.URI;

import uk.ac.manchester.cs.owl.semspreadsheets.impl.SheetHSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.impl.WorkbookHSSFImpl;

/**
 * Some helpful common utility methods for setting up spreadsheet tests
 * @author Stuart Owen 
 */
public class SpreadsheetTestHelper {
		
	public static WorkbookHSSFImpl getBlankWorkbook() throws Exception 
	{
		return new WorkbookHSSFImpl();
	}
	
	public static SheetHSSFImpl getWorkbookSheet(URI resourceURI, int index) throws Exception {
		WorkbookHSSFImpl book = openWorkbookHSSF(resourceURI);
		return (SheetHSSFImpl)book.getSheet(index);
	}
	
	public static WorkbookHSSFImpl openWorkbookHSSF(URI uri) throws Exception 
	{
		return new WorkbookHSSFImpl(uri);
	}
		
		
}
