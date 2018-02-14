/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Iterator;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.InvalidWorkbookFormatException;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * A simple CSV export, mainly for use to test exporting and as a debugging
 * tool.
 * 
 * @author Stuart Owen
 *
 */
public class CSVExporter extends AbstractExporter {

	public CSVExporter(File workbookFile) throws IOException, InvalidWorkbookFormatException {
		super(workbookFile);
	}

	public CSVExporter(URI workbookURI) throws IOException, InvalidWorkbookFormatException {
		super(workbookURI);
	}

	public CSVExporter(WorkbookManager manager) {
		super(manager);
	}

	@Override
	public void export(OutputStream outStream) {
		PrintWriter writer = new PrintWriter(outStream);
		writeHeader(writer);
		Iterator<PopulatedValidatedCellDetails> iterator = getPopulatedValidatedCellDetails().iterator();
		Iterator<Cell> cellIterator = getPoplulatedNonValidatedCells().iterator();

		while (iterator.hasNext()) {
			PopulatedValidatedCellDetails cellDetails = iterator.next();
			String csv = cellToCSV(cellDetails);
			writer.write(csv);
			if (cellIterator.hasNext() || iterator.hasNext()) {
				writer.write("\n");
			}
		}

		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			String csv = cellToCSV(cell);
			writer.write(csv);
			if (cellIterator.hasNext()) {
				writer.write("\n");
			}
		}

		writer.flush();
	}

	private void writeHeader(PrintWriter writer) {
		writer.write("text,col,row,sheet,type,term uri,entity uri,property uri,ontology uri,ontology source\n");
	}

	private String cellToCSV(Cell cell) {
		String notDefinedString = notDefinedString();
		String csv = "\"" + handleQuotes(cell.getValue()) + "\",";
		csv += cell.getColumn() + ",";
		csv += cell.getRow() + ",";
		csv += "\"" + handleQuotes(cell.getSheetName()) + "\",";
		csv += "Text," + notDefinedString + "," + notDefinedString + "," + notDefinedString + "," + notDefinedString
				+ "," + notDefinedString;

		return csv;
	}

	private String cellToCSV(PopulatedValidatedCellDetails cellDetails) {
		String csv = "\"" + cellDetails.getTextValue() + "\",";
		csv += cellDetails.getCell().getColumn() + ",";
		csv += cellDetails.getCell().getRow() + ",";
		csv += "\"" + handleQuotes(cellDetails.getSheet().getName()) + "\",";

		csv += handleQuotes(cellDetails.getValidation().getValidationDescriptor().getType().toString()) + ",";

		String termStr = notDefinedString();
		if (cellDetails.getTerm() != null) {
			termStr = handleQuotes(cellDetails.getTerm().getIRI().toString());
		}
		csv += "\"" + termStr + "\",";

		csv += "\"" + handleQuotes(cellDetails.getEntityIRI().toString()) + "\",";
		String propertyStr = notDefinedString();
		if (cellDetails.getOWLPropertyItem() != null) {
			propertyStr = cellDetails.getOWLPropertyItem().getIRI().toString();
		}
		csv += "\"" + handleQuotes(propertyStr) + "\",";
		csv += "\"" + handleQuotes(cellDetails.getOntologyIRIs().iterator().next().toString()) + "\",";
		csv += "\"" + handleQuotes(cellDetails.getPhysicalIRIs().iterator().next().toString()) + "\"";

		return csv;
	}

	// quotes should be doubled
	private String handleQuotes(String original) {
		return original.replaceAll("\"", "\"\"");
	}

	protected String notDefinedString() {
		return "None";
	}

}
