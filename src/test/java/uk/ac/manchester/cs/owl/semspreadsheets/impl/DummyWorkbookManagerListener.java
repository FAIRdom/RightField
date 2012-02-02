package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookManagerEvent;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookManagerListener;

class DummyWorkbookManagerListener implements
		WorkbookManagerListener {
	public boolean workbookLoadedFired = false;
	public boolean workbookChangedFired = false;
	public boolean validationAppliedFired = false;
	public boolean ontologiesChanedFired = false;
	
	public boolean isWorkbookLoadedFired() {
		return workbookLoadedFired;
	}

	public boolean isWorkbookChangedFired() {
		return workbookChangedFired;
	}

	public boolean isValidationAppliedFired() {
		return validationAppliedFired;
	}

	public boolean isOntologiesChanedFired() {
		return ontologiesChanedFired;
	}

	@Override
	public void workbookLoaded(WorkbookManagerEvent event) {
		workbookLoadedFired=true;
	}

	@Override
	public void workbookChanged(WorkbookManagerEvent event) {
		workbookChangedFired=true;
	}

	@Override
	public void validationAppliedOrCancelled() {
		validationAppliedFired=true;
	}

	@Override
	public void ontologiesChanged(WorkbookManagerEvent event) {
		ontologiesChanedFired=true;
	}
}