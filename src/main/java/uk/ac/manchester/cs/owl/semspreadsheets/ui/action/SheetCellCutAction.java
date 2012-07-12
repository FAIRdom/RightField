/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.SetCellValue;

/**
 * Action to handle 'cutting' a range of cells from a sheet
 * 
 * @author Stuart Owen
 * 
 */
@SuppressWarnings("serial")
public class SheetCellCutAction extends SheetCellCopyAction {

	private static Logger logger = Logger.getLogger(SheetCellCutAction.class);

	public SheetCellCutAction(WorkbookManager workbookManager, Toolkit toolkit) {
		super("Cut", workbookManager, toolkit);
		setAcceleratorKey(KeyEvent.VK_X);	
	}

	public void actionPerformed(ActionEvent e) {
		logger.debug("Cut action invoked");
		super.actionPerformed(e);
		
		Range selectedRange = getSelectedRange();
		if (selectedRange.count()<=MAX_CELLS) { 
			if (selectedRange.isCellSelection()) {
				for (int col = selectedRange.getFromColumn(); col < selectedRange
						.getToColumn() + 1; col++) {
					for (int row = selectedRange.getFromRow(); row < selectedRange
							.getToRow() + 1; row++) {
						Cell cell = selectedRange.getSheet().getCellAt(col, row);
						// FIXME: for some reason, remove validations on an
						// entire range in one go isn't working
						// so for now remove each one individually					
						getWorkbookManager().removeValidations(
								new Range(
										selectedRange.getSheet(), col, row, col,
										row));
						if (cell != null) {
							String oldValue = cell.getValue();
							SetCellValue change = new SetCellValue(
									selectedRange.getSheet(), col, row, oldValue,
									null);
							getWorkbookManager().applyChange(change);
						}						
					}
				}			
			}
		}
	}	
}
