package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import org.apache.log4j.Logger;

/**
 * Stores the state of the workbook
 * 
 * @author Stuart Owen
 *
 */
public class WorkbookState {
	
	private static Logger logger = Logger.getLogger(WorkbookState.class);
	
	private boolean changesSaved = true;

	public boolean isChangesSaved() {
		return changesSaved;
	}

	public void changesSaved() {
		this.changesSaved = true;
		logger.debug("Changes saved");
	}
	
	public void changesUnsaved() {				
		this.changesSaved = false;
		logger.debug("Unsaved changes");
	}

}
