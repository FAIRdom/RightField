/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookManagerEvent;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookManagerListener;

/**
 * 
 * @author Stuart Owen
 *
 */
class DummyWorkbookManagerListener implements
		WorkbookManagerListener {
	public boolean workbookLoadedFired = false;
	public boolean workbookChangedFired = false;
	public boolean validationAppliedFired = false;
	public boolean ontologiesChanedFired = false;
	
	public DummyWorkbookManagerListener() {
		reset();
	}
	
	//resets the state of all flags
	public void reset() {
		workbookLoadedFired = false;
		workbookChangedFired = false;
		validationAppliedFired = false;
		ontologiesChanedFired = false;
	}
	
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
	public void workbookCreated(WorkbookManagerEvent event) {
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
