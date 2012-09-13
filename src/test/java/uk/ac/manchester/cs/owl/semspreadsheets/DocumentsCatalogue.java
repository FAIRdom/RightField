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
	
	public static URI mgedOntologyURI() throws Exception {
		return uriForResourceName("MGEDOntology.owl");
	}
	
	public static URI aminoAcidOntologyURI() throws Exception {
		return uriForResourceName("amino-acid.owl");
	}
	
	public static URI scoroOntologyURI() throws Exception {
		return uriForResourceName("scoro.owl");
	}
	
	//A workbook with the cells B2:D5 applied with FreeText and property http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber
	public static URI simpleWorkbookWithLiteralsOverRangeURI() throws Exception {
		return uriForResourceName("literals_only_over_range.xls");
	}
	
	//A workbook with the cells B2:D5 applied with FreeText and property http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber
		public static URI simpleWorkbookWithLiteralsOverRangeXLSXURI() throws Exception {
			return uriForResourceName("literals_only_over_range.xlsx");
		}
	
	public static URI simpleAnnotatedworkbookURI() throws Exception {
		return uriForResourceName("simple_annotated_book.xls");
	}
	
	public static URI simpleAnnotatedXLSXWorkbookURI() throws Exception {
		return uriForResourceName("simple_annotated_book.xlsx");
	}
	
	public static URI simpleWorkbookURI() throws Exception {
		return uriForResourceName("simple_book.xls");
	}
	
	public static URI bookWithPropertiesURI() throws Exception {
		return uriForResourceName("book_with_properties.xls");
	}
	
	public static URI bookWithPropertiesXLSXURI() throws Exception {
		return uriForResourceName("book_with_properties.xlsx");
	}
	
	public static URI partiallyPopulatedWorkbookURI() throws Exception {
		return uriForResourceName("partially_populated_JERM_template.xls");		
	}
	
	public static File populatedJermWorkbookFile() throws Exception {
		return fileForResourceName("populated_JERM_template.xls");
	}
	
	public static File populatedJermWorkbookXLSXFile() throws Exception {
		return fileForResourceName("populated_JERM_template.xlsx");
	}
	
	public static URI populatedJermWorkbookURI() throws Exception {
		return uriForResourceName("populated_JERM_template.xls");
	}	
	
	public static URI populatedJermWorkbookXLSXURI() throws Exception {
		return uriForResourceName("populated_JERM_template.xlsx");
	}
	
	public static URI twoOntologiesWorkbookURI() throws Exception {
		return uriForResourceName("two_ontologies.xls");
	}
	
	public static URI twoOntologiesWorkbookXLSXURI() throws Exception {
		return uriForResourceName("two_ontologies.xlsx");
	}
	
	public static URI simpleExcel2007WorkbookURI() throws Exception {
		return uriForResourceName("simple_excel2007.xlsx");
	}
	
	public static URI nonExistantFileURI() throws Exception {
		URI uri = DocumentsCatalogue.class.getResource("/").toURI();
		return new URI(uri.toString()+"dont_exist.xls");
	}
	
	public static URI workbookWithColoursXLSXURI() throws Exception {
		return uriForResourceName("spreadsheet_with_colours.xlsx");
	}
	
	public static URI workbookWithColoursURI() throws Exception {
		return uriForResourceName("spreadsheet_with_colours.xls");
	}
	
	private static URI uriForResourceName(String resource) throws Exception {
		return DocumentsCatalogue.class.getResource("/"+resource).toURI();
	}
		
	
	private static File fileForResourceName(String resource) throws Exception {
		String filename = DocumentsCatalogue.class.getResource("/"+resource).getFile();
		return new File(filename);
	}

}
