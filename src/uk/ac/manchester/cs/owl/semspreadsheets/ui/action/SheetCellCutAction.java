package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.change.SetCellValue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Action to handle 'cutting' a cell from a sheet
 * 
 * @author Stuart Owen
 *
 */
@SuppressWarnings("serial")
public class SheetCellCutAction extends SheetCellCopyAction {

	private static Logger logger = Logger.getLogger(SheetCellCutAction.class);

	public SheetCellCutAction(WorkbookManager workbookManager, Toolkit toolkit) {
		super("Cut", workbookManager, toolkit);
	}

	public void actionPerformed(ActionEvent e) {
		logger.debug("Cut action invoked");
		super.actionPerformed(e);

		Range selectedRange = getSelectedRange();
		if (selectedRange.isCellSelection()) {
			if (selectedRange.isSingleCellSelected()) {
				int row = selectedRange.getFromRow();
				int col = selectedRange.getFromColumn();
				Cell cell = selectedRange.getSheet().getCellAt(col, row);
				if (cell != null) {
					String oldValue = cell.getValue();
					SetCellValue change = new SetCellValue(
							selectedRange.getSheet(), col, row, oldValue, null);
					getWorkbookManager().applyChange(change);
				}
				getWorkbookManager().getOntologyTermValidationManager().removeValidation(selectedRange);
			}
		}
	}

}
