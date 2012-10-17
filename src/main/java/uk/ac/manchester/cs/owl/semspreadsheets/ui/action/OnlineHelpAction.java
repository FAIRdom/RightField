/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class OnlineHelpAction extends WorkbookFrameAction {

	private String HELP_URI="http://www.rightfield.org.uk/guide";
	
	private static Logger logger = Logger.getLogger(OnlineHelpAction.class);
	
	public OnlineHelpAction(WorkbookFrame workbookFrame) {
		super("Online Help",workbookFrame);
		
	}		
	
	@Override
	public void actionPerformed(ActionEvent action) {
		try {
			Desktop.getDesktop().browse(URI.create(HELP_URI));
		} catch (IOException e) {
			logger.error("There was a problem opening the URI: " + HELP_URI, e);
			ErrorHandler.getErrorHandler().handleError(e);
		}
	}

}
