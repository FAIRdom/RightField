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
 * from XLSX spreadsheets the formula to be decoded can look like AND(B2<>\"propliteral^sheetX\") because of the way XSSF stores and retrieves its validations
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
		logger.debug("Decoding formula: "+formula);		
		String decoded = null;
		if (valid(formula)) {
			String [] bits = formula.split("\\"+PropertyValidationForumlaDefinition.SEPERATOR);
			if (bits.length==2) {
				decoded = bits[1].trim();	
				if (!formula.toLowerCase().startsWith("propliteral")) {
					decoded=decoded.substring(0,decoded.length()-2);
				}
			}
			else {
				//FIXME: should raise exception rather than just return null
				logger.error("Unexpected number of elements in encoded property "+formula);
			}  
		}
		else {
			//FIXME: should raise exception rather than just return null
			logger.warn("Attempt to decode invalid validation formula for property validation: "+formula);
		}
		  
		logger.debug("Decoded formula "+formula+" to "+decoded);
		return decoded;
	}
	
	public static boolean valid(String formula) {
		boolean valid=false;
		String [] bits = formula.split("\\"+PropertyValidationForumlaDefinition.SEPERATOR);
		if (bits.length==2) {
			if (bits[0].contains("propliteral")) {
				if (!bits[1].trim().isEmpty()) {
					valid=true;
				}
			}
		}
		return valid;
	}
	
}
