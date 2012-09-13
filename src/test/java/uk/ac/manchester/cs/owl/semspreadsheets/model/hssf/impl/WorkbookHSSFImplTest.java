/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.SpreadsheetTestHelper;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.impl.GeneralWorkbookTests;


public class WorkbookHSSFImplTest extends GeneralWorkbookTests {
	
	
	protected Workbook getEmptyWorkbook() throws Exception {
		return SpreadsheetTestHelper.getBlankWorkbook();
	}
	
	//opens the workbook src/test/resources/simple_annotated_book.xls
	protected Workbook getTestWorkbook() throws Exception {
		return SpreadsheetTestHelper.openWorkbookHSSF(DocumentsCatalogue.simpleAnnotatedworkbookURI());
	}
	
	protected String getExtension() {
		return "xls";
	}
	
	
}
