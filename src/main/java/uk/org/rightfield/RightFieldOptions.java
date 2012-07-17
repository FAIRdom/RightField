/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.org.rightfield;

import org.apache.log4j.Logger;

/**
 * A very basic command line argument handler, primarily for picking out export arguments. Not very robust as its only really intended for internal use.
 * 
 * When/if additional arguments need to be fully supported, then this will need updating using a 3rd party library such as Apache Commons.
 * 
 * @author Stuart Owen
 *
 */
public class RightFieldOptions {
	
	private static final Logger logger = Logger.getLogger(RightFieldOptions.class);
	
	String id="df:1";
	String exportFormat="rdf";
	boolean export=false;
	String filename=null;
	String property=null;
	private final String[] args;
	
	public RightFieldOptions(String [] args) {
		this.args = args;
		readArgs();
	}	

	public String getProperty() {
		return property;
	}
	
	public int count() {
		return args.length;
	}
	
	public String getId() {
		return id;
	}

	public String getExportFormat() {
		return exportFormat;
	}

	public boolean isExport() {
		return export;
	}

	public String getFilename() {
		return filename;
	}	
	
	@Override
	public String toString() {
		String result = "";
		result += "Export: "+isExport();
		result += ", Format: "+getExportFormat();
		result += ", ID: "+getId();
		result += ", Filename: "+getFilename();
		return result;
	}

	private void readArgs() {
		for (int i=0;i<args.length;i++) {
			if (args[i].equals("-export")) {
				export=true;
			}
			if (args[i].equals("-id")) {
				id=args[i+1];
				i++;
			}
			if (args[i].equals("-format")) {
				exportFormat=args[i+1];
				i++;
			}
			if (args[i].equals("-property")) {
				property=args[i+1];
				i++;
			}
		}
		if (count()>0) {
			filename=args[count()-1];
		}
		logger.debug(this.toString());
	}
}
