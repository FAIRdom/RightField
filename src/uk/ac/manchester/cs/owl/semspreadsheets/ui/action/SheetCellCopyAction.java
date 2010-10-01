package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Collection;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
/**
 * Action to handle 'copying' a cell from a sheet
 * 
 * @author Stuart Owen
 *
 */
@SuppressWarnings("serial")
public class SheetCellCopyAction extends SelectedCellsAction {

	private static Logger logger = Logger.getLogger(SheetCellCopyAction.class);
	private final Toolkit toolkit;

	public SheetCellCopyAction(WorkbookManager workbookManager, Toolkit toolkit) {
		this("Copy", workbookManager, toolkit);
	}

	protected SheetCellCopyAction(String name, WorkbookManager workbookManager,
			Toolkit toolkit) {
		super(name, workbookManager);
		this.toolkit = toolkit;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug("Copy action invoked");
		Range selectedRange = getSelectedRange();		
		if (selectedRange.isCellSelection()) {
			if (selectedRange.isSingleCellSelected()) {
				Collection<OntologyTermValidation> containingValidations = getWorkbookManager()
						.getOntologyTermValidationManager()
						.getContainingValidations(selectedRange);
				logger.debug("Selected validations = " + containingValidations);
				
				int row = selectedRange.getFromRow();
				int col = selectedRange.getFromColumn();
				Cell cell = selectedRange.getSheet().getCellAt(col, row);
				String textValue = "";
				if (cell != null) {
					textValue = cell.getValue();
				} else {
					logger.debug("Selected cell is returned as NULL");
					// we assume that we are copying an empty value, rather than
					// leaving the clipboard intact
					textValue = "";
				}
				
				Transferable tr = new CellContentsTransferable(textValue,containingValidations);
								
				Clipboard clippy = toolkit.getSystemClipboard();
				clippy.setContents(tr, null);
			} else {
				logger.info("Copying a range of cells is not yet supported");
			}
		} else {
			logger.info("Nothing selected");
		}

	}

}
