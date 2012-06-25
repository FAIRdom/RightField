/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.apache.log4j.Logger;

/**
 * Encodes and decodes the string that defines that a free text property hidden sheet name that gets embedded in the validation, along with the hidden sheet name
 * 
 * e.g if hidden sheet name is 'sheetxxx' then the encoding is:
 * propliteral^sheetxxx
 * 
 * @author Stuart Owen
 *
 */
public class PropertyValidationForumlaDefinition {
	
	private static final Logger logger = Logger.getLogger(PropertyValidationForumlaDefinition.class);
	
	/**
	 * Defines the seperator used in the encoding, currently ^ is chosen since it is not a valid IRI character, so makes splitting the string easier.
	 */
	public static final String SEPERATOR = "^";
	
	public static String encode(String hiddenSheetName) {
		return "propliteral"+SEPERATOR+hiddenSheetName;
	}

	public static String decode(String formula) {
		String name = null;
		String [] bits = formula.split("\\"+PropertyValidationForumlaDefinition.SEPERATOR);
		if (bits.length==2) {
			name = bits[1].trim();															
		}
		else {
			//FIXME: should raise exception rather than just return null
			logger.warn("Unexpected number of elements in encoded property "+formula);
		}    

		return name;
	}
	
	public static boolean valid(String formula) {
		boolean valid=false;
		String [] bits = formula.split("\\"+PropertyValidationForumlaDefinition.SEPERATOR);
		if (bits.length==2) {
			if (bits[0].equals("propliteral")) {
				if (!bits[1].trim().isEmpty()) {
					valid=true;
				}
			}
		}
		return valid;
	}
	
}
