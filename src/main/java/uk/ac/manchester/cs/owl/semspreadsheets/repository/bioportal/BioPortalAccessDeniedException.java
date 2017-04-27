/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

@SuppressWarnings("serial")
public class BioPortalAccessDeniedException extends Exception {	
	public BioPortalAccessDeniedException() {
		super("Invalid API Key");
	}
}
