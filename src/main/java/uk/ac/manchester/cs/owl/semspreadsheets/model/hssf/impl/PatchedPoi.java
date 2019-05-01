/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.hssf.usermodel.HSSFSheet;

/**
 * This class isolates the interaction with Apache POI that relies on following accessor methods that were added to POI in the
 * patch. Hopefully one day this patching won't be necessary
 * 
 * {@link HSSFSheet#getDataValidityTable()}
 * {@link DataValidityTable#clear()}
 * {@link DVRecord#getFormula1()}
 * {@link DVRecord#getFormula2()}
 * 
 * @author Stuart Owen 
 */
public class PatchedPoi {

	private static PatchedPoi instance = new PatchedPoi();

	private PatchedPoi() {

	}

	public static PatchedPoi getInstance() {
		return instance;
	}


	public void clearValidationData(HSSFSheet sheet) {		
		sheet.getDataValidityTable().clear();
	}

}
