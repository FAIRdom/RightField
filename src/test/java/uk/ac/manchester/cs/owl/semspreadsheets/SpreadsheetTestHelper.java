/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets;

import java.io.File;
import java.net.URI;
import java.util.UUID;

import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.SheetHSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.WorkbookHSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl.SheetXSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl.WorkbookXSSFImpl;

/**
 * Some helpful common utility methods for setting up spreadsheet tests
 * @author Stuart Owen 
 */
public class SpreadsheetTestHelper {
		
	public static WorkbookHSSFImpl getBlankWorkbook() throws Exception 
	{
		return new WorkbookHSSFImpl();
	}
	
	public static WorkbookXSSFImpl getBlankXSSFWorkbook() throws Exception 
	{
		return new WorkbookXSSFImpl();
	}
	
	public static SheetHSSFImpl getWorkbookSheet(URI resourceURI, int index) throws Exception {
		WorkbookHSSFImpl book = openWorkbookHSSF(resourceURI);
		return (SheetHSSFImpl)book.getSheet(index);
	}
	
	public static SheetXSSFImpl getWorkbookSheetXSSF(URI resourceURI, int index) throws Exception {
		WorkbookXSSFImpl book = openWorkbookXSSF(resourceURI);
		return (SheetXSSFImpl)book.getSheet(index);
	}
	
	public static WorkbookHSSFImpl openWorkbookHSSF(URI uri) throws Exception 
	{
		return new WorkbookHSSFImpl(uri);
	}
	
	public static WorkbookXSSFImpl openWorkbookXSSF(URI uri) throws Exception 
	{
		return new WorkbookXSSFImpl(uri);
	}
	
	public static File getTempFile(String extension) {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		
		String uuid = UUID.randomUUID().toString();
		return new File(tmpDir,uuid+"."+extension);
	}
		
		
}
