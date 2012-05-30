/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeEvent;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;

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
	
	protected boolean isWorkbookChangedFired() {
		return isWorkbookChangedFired;
	}

	protected boolean isSheetAddedFired() {
		return isSheetAddedFired;
	}

	protected boolean isSheetRemovedFired() {
		return isSheetRemovedFired;
	}

	protected boolean isSheetRenamedFired() {
		return isSheetRenamedFired;
	}

}
