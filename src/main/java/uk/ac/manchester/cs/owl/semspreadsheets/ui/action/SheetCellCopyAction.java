/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.JOptionPane;

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
	
	protected int MAX_CELLS=500;

	private static Logger logger = Logger.getLogger(SheetCellCopyAction.class);
	private final Toolkit toolkit;

	public SheetCellCopyAction(WorkbookManager workbookManager, Toolkit toolkit) {
		this("Copy", workbookManager, toolkit);
		setAcceleratorKey(KeyEvent.VK_C);
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
		SelectedCellDataContainerList selectedContents = new SelectedCellDataContainerList();
		if (selectedRange.isCellSelection()) {
			if (selectedRange.count() <= MAX_CELLS) {
				for (int col = selectedRange.getFromColumn(); col < selectedRange
						.getToColumn() + 1; col++) {
					for (int row = selectedRange.getFromRow(); row < selectedRange
							.getToRow() + 1; row++) {
						SelectedCellDataContainer cellContent = new SelectedCellDataContainer();
						cellContent.row = row - selectedRange.getFromRow();
						cellContent.col = col - selectedRange.getFromColumn();

						Range singleCellRange = new Range(
								selectedRange.getSheet(), col, row, col, row);
						Collection<OntologyTermValidation> containingValidations = getWorkbookManager()
								.getOntologyTermValidationManager()
								.getContainingValidations(singleCellRange);
						if (containingValidations.size() > 0) {
							cellContent.validationDescriptor = containingValidations
									.iterator().next()
									.getValidationDescriptor();
						}
						Cell cell = selectedRange.getSheet()
								.getCellAt(col, row);

						if (cell != null) {
							cellContent.textValue = cell.getValue();
						} else {
							logger.debug("Selected cell is returned as NULL");
							// we assume that we are copying an empty value,
							// rather
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
				JOptionPane.showMessageDialog(null, "A maximum of "+MAX_CELLS+" cells can be placed on the clipboard.","Too many cells",JOptionPane.WARNING_MESSAGE);
			}

		} else {
			logger.info("Nothing selected");
		}

	}

}
