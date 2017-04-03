/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.export.CSVExporter;
import uk.ac.manchester.cs.owl.semspreadsheets.export.Exporter;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.CSVExportResultPanel;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * @author Stuart Owen
 */

@SuppressWarnings("serial")
public class ExportCSVAction extends WorkbookFrameAction {
	
	public ExportCSVAction(WorkbookFrame frame) {
		super("Export as CSV...",frame);		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Exporter exporter = new CSVExporter(getWorkbookManager());
		try {
			String csv = exporter.export();
			CSVExportResultPanel.showDialog(getWorkbookFrame(), csv);
		}
		catch(Exception ex) {
			ErrorHandler.getErrorHandler().handleError(ex);
		}
	}

}
