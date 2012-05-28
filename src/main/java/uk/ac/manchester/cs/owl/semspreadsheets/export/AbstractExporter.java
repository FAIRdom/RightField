package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public abstract class AbstractExporter {
	
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
