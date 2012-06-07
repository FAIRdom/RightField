/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

public enum OWLPropertyType {
	
	DATA_PROPERTY("DATA_PROPERTY"),
	OBJECT_PROPERTY("OBJECT_PROPERTY");
	
	private String str;

	OWLPropertyType(String str) {
		this.str = str;
	}

	@Override
	public String toString() {
		return str;
	}
		
}
