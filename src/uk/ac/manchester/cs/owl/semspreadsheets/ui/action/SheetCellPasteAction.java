package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.change.SetCellValue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

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
				DataFlavor df = DataFlavor.stringFlavor;
				if (contents.isDataFlavorSupported(df)) {
					try {
						String str = (String) contents.getTransferData(df);
						logger.debug("Read from system clipboard:" + str);
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
								str);
						getWorkbookManager().applyChange(change);
					} catch (UnsupportedFlavorException e1) {
						logger.warn(e1);
					} catch (IOException e1) {
						logger.warn(e1);
					}
				}
			} else {
				logger.info("Pasting into a range of cells is not yet supported");
			}
		} else {
			logger.info("Nothing selected");
		}
	}
}
