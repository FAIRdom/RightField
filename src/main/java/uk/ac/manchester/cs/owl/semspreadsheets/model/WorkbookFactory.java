/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl.WorkbookHSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl.WorkbookXSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFormat;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class WorkbookFactory {
	
	private static final Logger logger = LogManager.getLogger();

    /**
     * Creates an empty workbook, defaulting to Excel 97 (HSSF) format
     * @return The workbook
     */
    public static Workbook createWorkbook() {
        return createWorkbook(WorkbookFormat.EXCEL97);
    }       
    
    /**
     * Creates either an Excel 97 (HSSF) or Excel 2007 (XSSF) workbook according to the format passed in
     * 
     */
    public static Workbook createWorkbook(WorkbookFormat format) {
    	logger.debug("Format requested: "+format);
    	if (format.equals(WorkbookFormat.EXCEL97)) {
    		logger.debug("Creating HSSF Workbook");
    		return new WorkbookHSSFImpl();
    	}
    	else if (format.equals(WorkbookFormat.EXCEL2007)) {
    		logger.debug("Creating XSSF Workbook");
    		return new WorkbookXSSFImpl();
    	}
    	else {
    		logger.debug("Unexpected format: "+format+" default to Excel 97");
    		return new WorkbookHSSFImpl();
    	}
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
				wb = new WorkbookXSSFImpl((XSSFWorkbook)created);
			}
		} catch (InvalidFormatException e) {
			throw new InvalidWorkbookFormatException(e, uri);
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkbookFormatException(e, uri);
		}
        return wb;
    }

}
