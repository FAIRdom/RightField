/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.InvalidWorkbookFormatException;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationDescriptor;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Term;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Handles the common tasks of exporting populated data validations, allowing subclasses to export in their varous flavours.
 * The key method is {@link #getPopulatedValidatedCellDetails()} which returns a list of details of all the cells that have been annotated, and a term selected,
 * together with information about where that term came from.
 * 
 * @author Stuart Owen
 * 
 * @see PopulatedValidatedCellDetails
 */
public abstract class AbstractExporter implements Exporter {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AbstractExporter.class);
	
	private final WorkbookManager manager;

	
	public AbstractExporter(WorkbookManager manager) {
		this.manager = manager;		
	}
	
	public AbstractExporter(URI workbookURI) throws IOException,InvalidWorkbookFormatException {
		this.manager = new WorkbookManager();
		this.manager.loadWorkbook(workbookURI);		
	}
	
	public AbstractExporter(File workbookFile) throws IOException,InvalidWorkbookFormatException {
		this.manager = new WorkbookManager();
		this.manager.loadWorkbook(workbookFile);		
	}
	
	public String export() {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		
		export(outStream);
		
		return outStream.toString();
	}
	
	protected List<Cell> getPoplulatedNonValidatedCells() {
		List<Cell> nonValidatedCells = new ArrayList<Cell>();
		Iterator<PopulatedValidatedCellDetails> iterator = getPopulatedValidatedCellDetails().iterator();
		List<Cell> validatedCells = new ArrayList<Cell>();
		while (iterator.hasNext()) {
			validatedCells.add(iterator.next().getCell());
		}
		for (Sheet sheet : getWorkbook().getVisibleSheets()) {
			for (Cell cell : sheet.getCellsWithContent()) {
				if (!validatedCells.contains(cell)) {
					nonValidatedCells.add(cell);
				}
			}
		}
		return nonValidatedCells;
	}
	
	protected List<PopulatedValidatedCellDetails> getPopulatedValidatedCellDetails() {
		ArrayList<PopulatedValidatedCellDetails> result = new ArrayList<PopulatedValidatedCellDetails>();
		for (OntologyTermValidation validation : getValidations()) {
			
				Range range = validation.getRange();
				OntologyTermValidationDescriptor validationDescriptor = validation.getValidationDescriptor();
				for (Cell cell : range.getCells()) {
					String value = cell.getValue();
					if (validationDescriptor.definesLiteral()) {
						if (value!=null) {
							PopulatedValidatedCellDetails pop = new PopulatedValidatedCellDetails(validation,cell,value);					
							result.add(pop);
						}
					}
					else {
						Term matchedTerm = null;
						for (Term term : validationDescriptor.getTerms()) {
							if (term.getFormattedName().equalsIgnoreCase(value) || term.getName().equalsIgnoreCase(value)) {
								matchedTerm = term;
								break;
							}
						}
						if (matchedTerm!=null) {
							PopulatedValidatedCellDetails pop = new PopulatedValidatedCellDetails(validation,cell,matchedTerm,value);					
							result.add(pop);
						}
					}
					
				}								
		}
		Collections.sort(result);
		return result;
	}
	
	protected WorkbookManager getWorkbookManager() {
		return this.manager;
	}
	
	protected OntologyManager getOntologyManager() {
		return getWorkbookManager().getOntologyManager();
	}
	
	protected Workbook getWorkbook() {
		return getWorkbookManager().getWorkbook();		
	}
	
	protected Collection<OntologyTermValidation> getValidations() {
		return getOntologyManager().getOntologyTermValidations();
	}
}
