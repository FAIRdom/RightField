package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.change.SetCellValue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationDescriptor;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Action to handle 'pasting' into a cell
 * 
 * @author Stuart Owen
 *
 */
@SuppressWarnings("serial")
public class SheetCellPasteAction extends SelectedCellsAction {

	private static Logger logger = Logger.getLogger(SheetCellPasteAction.class);

	private final Toolkit toolkit;

	public SheetCellPasteAction(WorkbookManager workbookManager, Toolkit toolkit) {
		super("Paste", workbookManager);
		this.toolkit = toolkit;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug("Paste action invoked");
		Range selectedRange = getSelectedRange();
		if (selectedRange.isCellSelection()) {
			if (selectedRange.isSingleCellSelected()) {
				Transferable contents = toolkit.getSystemClipboard()
						.getContents(null);
				DataFlavor df = CellContentsTransferable.dataFlavour;
				if (contents.isDataFlavorSupported(df)) {
					Object[] dataValues;
					try {
						dataValues = (Object[])contents.getTransferData(df);
						logger.debug("OntologyValidationsTransferable contents found");
						Object textValue=dataValues[0];
						@SuppressWarnings("unchecked")
						OntologyTermValidationDescriptor descriptor = (OntologyTermValidationDescriptor)dataValues[1];
						pasteTextValue(selectedRange, textValue);
						pasteValidations(selectedRange, descriptor);
					} catch (UnsupportedFlavorException e1) {
						logger.error("Unsupported flavour.",e1);
					} catch (IOException e1) {
						logger.error("Error reading from clipboard.",e1);
					}
				}
				else if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					try {
						String textValue = (String)contents.getTransferData(DataFlavor.stringFlavor);
						pasteTextValue(selectedRange, textValue);
					} catch (UnsupportedFlavorException e1) {
						logger.error("Unsupported flavour.",e1);
					} catch (IOException e1) {
						logger.error("Error reading from clipboard.",e1);
					}
				}
			} else {
				logger.info("Pasting into a range of cells is not yet supported");
			}
		} else {
			logger.info("Nothing selected");
		}
	}

	private void pasteValidations(Range selectedRange,
			OntologyTermValidationDescriptor descriptor) {
		getWorkbookManager().removeValidations(selectedRange);
		if (descriptor!=null) {			
			getWorkbookManager().setValidationType(descriptor.getType(), descriptor.getEntityIRI());
		}
	}

	private void pasteTextValue(Range selectedRange, Object textValue) {
		int row = selectedRange.getFromRow();
		int col = selectedRange.getFromColumn();
		Cell cell = selectedRange.getSheet()
				.getCellAt(col, row);
		Object oldValue = null;
		if (cell != null) {
			oldValue = cell.getValue();
		}
		SetCellValue change = new SetCellValue(
				selectedRange.getSheet(), col, row, oldValue,
				textValue);
		getWorkbookManager().applyChange(change);
	}
}
