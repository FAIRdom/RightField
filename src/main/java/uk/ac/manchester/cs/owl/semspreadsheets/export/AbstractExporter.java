package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationDescriptor;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Term;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Handles the common tasks of exporting populated data validations, allowing subclasses to export in their varous flavours.
 * The key method is {@link #getPopulatedValidatedCellDetails()} which returns a list of details of all the cells that have been annotated, and a term selected,
 * together with information about where that term came from.
 * 
 * @author Stuart Owen
 * @see PopulatedValidatedCellDetails
 */
public abstract class AbstractExporter implements Exporter {
	
	private final WorkbookManager manager;

	
	public AbstractExporter(WorkbookManager manager) {
		this.manager = manager;
	}
	
	public AbstractExporter(URI workbookURI) throws IOException {
		this.manager = new WorkbookManager();
		this.manager.loadWorkbook(workbookURI);
	}
	
	public AbstractExporter(File workbookFile) throws IOException {
		this.manager = new WorkbookManager();
		this.manager.loadWorkbook(workbookFile);
	}
	
	public String export() {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		export(outStream);
		return outStream.toString();
	}
	
	public Collection<PopulatedValidatedCellDetails> getPopulatedValidatedCellDetails() {
		ArrayList<PopulatedValidatedCellDetails> result = new ArrayList<PopulatedValidatedCellDetails>();
		for (OntologyTermValidation validation : getValidations()) {
			Range range = validation.getRange();
			OntologyTermValidationDescriptor validationDescriptor = validation.getValidationDescriptor();
			for (Cell cell : range.getCells()) {
				String value = cell.getValue();
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
		return result;
	}
	
	protected WorkbookManager getWorkbookManager() {
		return this.manager;
	}
	
	protected Workbook getWorkbook() {
		return getWorkbookManager().getWorkbook();		
	}
	
	protected Collection<OntologyTermValidation> getValidations() {
		return getWorkbookManager().getOntologyTermValidationManager().getValidations();
	}
}
