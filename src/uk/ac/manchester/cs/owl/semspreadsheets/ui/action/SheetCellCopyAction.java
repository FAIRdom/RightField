package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		List<SelectedCellDataContainer> selectedContents = new ArrayList<SelectedCellDataContainer>();
		if (selectedRange.isCellSelection()) {
			for (int col = selectedRange.getFromColumn(); col < selectedRange
					.getToColumn() + 1; col++) {
				for (int row = selectedRange.getFromRow(); row < selectedRange
						.getToRow() + 1; row++) {
					SelectedCellDataContainer cellContent = new SelectedCellDataContainer();
					cellContent.row = row - selectedRange.getFromRow();
					cellContent.col = col - selectedRange.getFromColumn();

					Range singleCellRange = new Range(selectedRange.getSheet(),
							col, row, col, row);
					Collection<OntologyTermValidation> containingValidations = getWorkbookManager()
							.getOntologyTermValidationManager()
							.getContainingValidations(singleCellRange);
					if (containingValidations.size() > 0) {
						cellContent.validationDescriptor = containingValidations
								.iterator().next().getValidationDescriptor();
					}
					Cell cell = selectedRange.getSheet().getCellAt(col, row);

					if (cell != null) {
						cellContent.textValue = cell.getValue();
					} else {
						logger.debug("Selected cell is returned as NULL");
						// we assume that we are copying an empty value, rather
						// than
						// leaving the clipboard intact
						cellContent.textValue = "";
					}
					selectedContents.add(cellContent);
				}
			}
			Transferable tr = new CellContentsTransferable(selectedContents);

			Clipboard clippy = toolkit.getSystemClipboard();
			clippy.setContents(tr, null);
		} else {
			logger.info("Nothing selected");
		}

	}

}
