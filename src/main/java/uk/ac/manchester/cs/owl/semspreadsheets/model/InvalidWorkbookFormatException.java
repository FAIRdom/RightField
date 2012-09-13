/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.net.URI;

/**
 * Exception related to trying to open a file format that is currently not supported
 * 
 * @author Stuart Owen
 */
@SuppressWarnings("serial")
public class InvalidWorkbookFormatException extends Exception {

	private final URI fileURI;

	public InvalidWorkbookFormatException(Throwable cause, URI fileURI) {
		super(cause);
		this.fileURI = fileURI;		
	}
	
	public URI getFileURI() {
		return fileURI;
	}	

	@Override
	public String getMessage() {
		return "The format of the file " + getFilename() + " is not supported by RightField.\nCurrently Microsoft Excel 97-2003 (XLS) and Excel 2007+ (XLSX) are supported";			
	}
	
	private String getFilename() {
		if (getFileURI().getScheme().equals("file")) {
			return getFileURI().getPath();			
		} else {
			return getFileURI().toString();
		}
	}
	
	
}
