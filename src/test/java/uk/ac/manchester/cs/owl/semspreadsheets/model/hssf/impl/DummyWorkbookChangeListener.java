/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.WorkbookChangeEvent;

public class DummyWorkbookChangeListener implements WorkbookChangeListener {
	
	private boolean isWorkbookChangedFired=false;	
	private boolean isSheetAddedFired=false;
	private boolean isSheetRemovedFired=false;
	private boolean isSheetRenamedFired=false;
			

	public void reset() {
		isWorkbookChangedFired=false;
		isSheetAddedFired=false;
		isSheetRemovedFired=false;
		isSheetRenamedFired=false;
	}
	
	@Override
	public void workbookChanged(WorkbookChangeEvent event) {
		isWorkbookChangedFired=true;
	}

	@Override
	public void sheetAdded() {
		isSheetAddedFired=true;
	}

	@Override
	public void sheetRemoved() {
		isSheetRemovedFired=true;
	}

	@Override
	public void sheetRenamed(String oldName, String newName) {
		isSheetRenamedFired=true;
	}
	
	public boolean isWorkbookChangedFired() {
		return isWorkbookChangedFired;
	}

	public boolean isSheetAddedFired() {
		return isSheetAddedFired;
	}

	public boolean isSheetRemovedFired() {
		return isSheetRemovedFired;
	}

	public boolean isSheetRenamedFired() {
		return isSheetRenamedFired;
	}

}
