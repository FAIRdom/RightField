/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.WorkbookHSSFImpl;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class WorkbookFactory {

    /**
     * Creates an empty workbook
     * @return The workbook
     */
    public static Workbook createWorkbook() {
        return new WorkbookHSSFImpl();
    }

    /**
     * Creates a workbook by parsing an Excel document at a given URI
     * @param uri The URI that points to the Excel workbook
     * @return A representation of the workbook at the specified URI
     * @throws IOException If there was an IO problem loading the workbook
     * @throws InvalidWorkbookFormatException indicates the format of the file behind the URI is not supported
     */
    public static Workbook createWorkbook(URI uri) throws IOException,InvalidWorkbookFormatException {
    	InputStream inputStream = uri.toURL().openStream();
    	Workbook wb = null;
        try {
			org.apache.poi.ss.usermodel.Workbook created = org.apache.poi.ss.usermodel.WorkbookFactory.create(inputStream);
			if (created instanceof HSSFWorkbook) {
				wb = new WorkbookHSSFImpl((HSSFWorkbook)created);
			}
			else {
				//wb = new WorkbookXSSFImpl((XSSFWorkbook)created);
				throw new InvalidXLSXWorkbookFormatException(uri);
			}
		} catch (InvalidFormatException e) {
			throw new InvalidWorkbookFormatException(e, uri);
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkbookFormatException(e, uri);
		}
        return wb;
    }

}
