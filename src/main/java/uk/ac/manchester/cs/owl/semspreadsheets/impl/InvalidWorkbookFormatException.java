/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.impl;

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
		if (getCause().getMessage().toLowerCase().contains("office 2007")) {
			String msg = "This format of " + getFilename() + " appears to be Microsoft Excel 2007+ (XLSX).\nThis version of RightField doesn't currently support this format.";
			msg+="\nThis is something we plan to support in the near future, however in the meantime it is safe to convert to and from Microsoft Excel 97-2003 (XLS) format without losing RightField added information.";
			return msg;
		}
		else {
			return "The format of the file " + getFilename() + " is not supported by RightField.\nCurrently only Microsoft Excel 97-2003 (XLS) is supported";
		}		
	}
	
	private String getFilename() {
		if (getFileURI().getScheme().equals("file")) {
			return getFileURI().getPath();			
		} else {
			return getFileURI().toString();
		}
	}
	
	
}
