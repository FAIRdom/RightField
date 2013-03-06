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
		return uriForResourceName("ontologies/JERM.owl");
	}

	public static URI mgedOntologyURI() throws Exception {
		return uriForResourceName("ontologies/MGEDOntology.owl");
	}

	public static URI aminoAcidOntologyURI() throws Exception {
		return uriForResourceName("ontologies/amino-acid.owl");
	}

	public static URI rdfSchemaOntologyURI() throws Exception {
		return uriForResourceName("ontologies/dwcterms_v2011-10-26.rdf");
	}

	public static URI scoroOntologyURI() throws Exception {
		return uriForResourceName("ontologies/scoro.owl");
	}

	// a simple workbook that contains cells with quotes and commas
	public static URI simpleWorkbookForCSVURI() throws Exception {
		return uriForResourceName("workbooks/workbookForCSVTest.xls");
	}

	// a simple xlsx workbook that contains cells with quotes and commas
	public static URI simpleXLSXWorkbookForCSVURI() throws Exception {
		return uriForResourceName("workbooks/workbookForCSVTest.xlsx");
	}

	// A workbook with the cells B2:D5 applied with FreeText and property
	// http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber
	public static URI simpleWorkbookWithLiteralsOverRangeURI() throws Exception {
		return uriForResourceName("workbooks/literals_only_over_range.xls");
	}

	// A workbook with the cells B2:D5 applied with FreeText and property
	// http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber
	public static URI simpleWorkbookWithLiteralsOverRangeXLSXURI()
			throws Exception {
		return uriForResourceName("workbooks/literals_only_over_range.xlsx");
	}

	public static URI simpleAnnotatedworkbookURI() throws Exception {
		return uriForResourceName("workbooks/simple_annotated_book.xls");
	}

	public static URI simpleAnnotatedXLSXWorkbookURI() throws Exception {
		return uriForResourceName("workbooks/simple_annotated_book.xlsx");
	}

	public static URI simpleWorkbookURI() throws Exception {
		return uriForResourceName("workbooks/simple_book.xls");
	}
	
	

	public static URI bookWithPropertiesURI() throws Exception {
		return uriForResourceName("workbooks/book_with_properties.xls");
	}

	public static URI bookWithPropertiesXLSXURI() throws Exception {
		return uriForResourceName("workbooks/book_with_properties.xlsx");
	}

	public static URI partiallyPopulatedWorkbookURI() throws Exception {
		return uriForResourceName("workbooks/partially_populated_JERM_template.xls");
	}

	public static File populatedJermWorkbookFile() throws Exception {
		return fileForResourceName("workbooks/populated_JERM_template.xls");
	}

	public static File populatedJermWorkbookXLSXFile() throws Exception {
		return fileForResourceName("workbooks/populated_JERM_template.xlsx");
	}

	public static URI populatedJermWorkbookURI() throws Exception {
		return uriForResourceName("workbooks/populated_JERM_template.xls");
	}

	public static URI populatedJermWorkbookXLSXURI() throws Exception {
		return uriForResourceName("workbooks/populated_JERM_template.xlsx");
	}

	public static URI twoOntologiesWorkbookURI() throws Exception {
		return uriForResourceName("workbooks/two_ontologies.xls");
	}

	public static URI twoOntologiesWorkbookXLSXURI() throws Exception {
		return uriForResourceName("workbooks/two_ontologies.xlsx");
	}

	public static URI simpleExcel2007WorkbookURI() throws Exception {
		return uriForResourceName("workbooks/simple_excel2007.xlsx");
	}

	public static URI nonExistantFileURI() throws Exception {
		URI uri = DocumentsCatalogue.class.getResource("/").toURI();
		return new URI(uri.toString() + "dont_exist.xls");
	}

	public static URI workbookWithColoursXLSXURI() throws Exception {
		return uriForResourceName("workbooks/spreadsheet_with_colours.xlsx");
	}

	public static URI workbookWithColoursURI() throws Exception {
		return uriForResourceName("workbooks/spreadsheet_with_colours.xls");
	}

	public static URI bookWithNumericsAndStringsURI() throws Exception {
		return uriForResourceName("workbooks/csv_problem_numerics.xls");
	}

	public static URI bookWithNumericsAndStringsXLSXURI() throws Exception {
		return uriForResourceName("workbooks/csv_problem_numerics.xlsx");
	}

	public static URI prideTemplateEmptyWorkbookURI() throws Exception {
		return uriForResourceName("workbooks/pride_template_empty.xls");
	}

	public static URI prideTemplateEmptyWorkbookXLSXURI() throws Exception {
		return uriForResourceName("workbooks/pride_template_empty.xlsm");
	}
	
	public static URI partiallyPopulatedMGEDWorkbookURI() throws Exception {
		 return uriForResourceName("workbooks/partially_populated_mged_book.xls");
	}

	private static URI uriForResourceName(String resource) throws Exception {
		return DocumentsCatalogue.class.getResource("/" + resource).toURI();
	}

	private static File fileForResourceName(String resource) throws Exception {
		String filename = DocumentsCatalogue.class.getResource("/" + resource)
				.getFile();
		return new File(filename);
	}

	

}
