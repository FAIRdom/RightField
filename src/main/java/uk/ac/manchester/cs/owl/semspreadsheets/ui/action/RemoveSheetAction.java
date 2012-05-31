/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class RemoveSheetAction extends WorkbookFrameAction {
	
	public RemoveSheetAction(WorkbookFrame workbookFrame) {
		super("Remove", workbookFrame);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		WorkbookFrame frame = getWorkbookFrame();
		Range range = frame.getWorkbookManager().getSelectionModel()
				.getSelectedRange();
		Sheet sheet = range.getSheet();
		if (frame.getWorkbookManager().getWorkbook().getVisibleSheets().size()>1) {
			int res = JOptionPane.showConfirmDialog(
					null,
					"Are you sure you want to remove the sheet '"
							+ sheet.getName()+"'", "Remove sheet?",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (res == JOptionPane.YES_OPTION) {
				frame.removeSheet(sheet);			
				Sheet firstSheet = frame.getWorkbookManager().getWorkbook().getVisibleSheets().get(0);
				frame.setSelectedSheet(firstSheet);				
			}
		}
		else {
			JOptionPane.showMessageDialog(null, "You cannot remove the final sheet");
		}		
	}

}
