/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets;

import java.io.File;
import java.net.URI;

public class DocumentsCatalogue {
	
	public static URI jermOntologyURI() throws Exception {
		return uriForResourceName("JERM.owl");
	}
	
	public static URI simpleAnnotatedworkbookURI() throws Exception {
		return uriForResourceName("simple_annotated_book.xls");
	}
	
	public static URI bookWithPropertiesURI() throws Exception {
		return uriForResourceName("book_with_properties.xls");
	}
	
	public static URI partiallyPopulatedWorkbookURI() throws Exception {
		return uriForResourceName("partially_populated_JERM_template.xls");		
	}
	
	public static File populatedJermWorkbookFile() throws Exception {
		return fileForResourceName("populated_JERM_template.xls");
	}
	
	public static URI populatedJermWorkbookURI() throws Exception {
		return uriForResourceName("populated_JERM_template.xls");
	}	
	
	public static URI twoOntologiesWorkbookURI() throws Exception {
		return uriForResourceName("two_ontologies.xls");
	}
	
	private static URI uriForResourceName(String resource) throws Exception {
		return DocumentsCatalogue.class.getResource("/"+resource).toURI();
	}
	
	private static File fileForResourceName(String resource) throws Exception {
		String filename = DocumentsCatalogue.class.getResource("/"+resource).getFile();
		return new File(filename);
	}

}
