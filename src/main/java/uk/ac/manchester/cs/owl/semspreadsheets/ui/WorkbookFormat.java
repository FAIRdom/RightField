/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui;

/**
 * 
 * @author Stuart Owen
 *
 */

public enum WorkbookFormat {
	
	EXCEL97("Excel 97-2003 (*.xls)"),
	EXCEL2007("Excel 2007+ (*.xlsx)");

	String label;		
	
	WorkbookFormat(String label) {
		this.label = label;
	}
	
	public static WorkbookFormat[] getFormats()  {
		return new WorkbookFormat[]{EXCEL97,EXCEL2007};
	}
	
	@Override
    public String toString() {
        return label;
    }
}
