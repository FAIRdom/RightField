/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

@SuppressWarnings("serial")
public class ApplyValidationAction extends WorkbookAction {

	public ApplyValidationAction(WorkbookManager workbookManager) {
		super("Apply",workbookManager);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		getWorkbookManager().applyValidationChange();		
	}

}
