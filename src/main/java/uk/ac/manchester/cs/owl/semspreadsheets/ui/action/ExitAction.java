/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class ExitAction extends WorkbookFrameAction {

	public ExitAction(WorkbookFrame frame) {
		super("Exit",frame);
	}
	@Override
	public void actionPerformed(ActionEvent action) {		
		getWorkbookFrame().exit();
	}

}
