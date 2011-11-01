package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import java.net.URI;

/**
 * Some helpful common utility methods for setting up spreadsheet tests
 * @author Stuart Owen 
 */
public class SpreadsheetTestHelper {

	public static WorkbookHSSFImpl openWorkbookHSSF(String resourceName) throws Exception 
	{
		URI uri = SpreadsheetTestHelper.class.getResource("/"+resourceName).toURI();
		return openWorkbookHSSF(uri);
	}
	
	public static SheetHSSFImpl getWorkbookSheet(String resourceName, int index) throws Exception {
		WorkbookHSSFImpl book = openWorkbookHSSF(resourceName);
		return (SheetHSSFImpl)book.getSheet(index);
	}
	
	private static WorkbookHSSFImpl openWorkbookHSSF(URI uri) throws Exception 
	{
		return new WorkbookHSSFImpl(uri);
	}
		
}
