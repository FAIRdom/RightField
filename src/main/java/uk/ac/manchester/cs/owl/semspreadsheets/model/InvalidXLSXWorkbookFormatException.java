package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.net.URI;

@SuppressWarnings("serial")
public class InvalidXLSXWorkbookFormatException extends
		InvalidWorkbookFormatException {
	
	public InvalidXLSXWorkbookFormatException(URI fileURI) {
		super(null,fileURI);
	}

	@Override
	public String getMessage() {
		String msg = "This format of " + getFilename() + " appears to be Microsoft Excel 2007+ (XLSX).\nThis version of RightField doesn't currently support this format.";
		msg+="\nThis is something we plan to support in the near future, however in the meantime it is safe to convert to and from Microsoft Excel 97-2003 (XLS) format without losing RightField added information.";
		return msg;
	}
	
	

}
