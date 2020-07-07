/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.*;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.SetCellValue;

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
		setAcceleratorKey(KeyEvent.VK_V);
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
					try {
						SelectedCellDataContainerList dataValues = (SelectedCellDataContainerList)contents.getTransferData(df);
						logger.debug("CellContentsList contents found");
						
						int row=selectedRange.getFromRow();
						int col=selectedRange.getFromColumn();
						for (SelectedCellDataContainer cellContent : dataValues) {
							Range cellRange = new Range(selectedRange.getSheet(),col+cellContent.col,row+cellContent.row,col+cellContent.col,row+cellContent.row);							
							pasteValidations(cellRange, cellContent.validationDescriptor);
							pasteTextValue(cellRange, cellContent.textValue);
						}
					} catch (UnsupportedFlavorException e1) {
						logger.error("Unsupported flavour.",e1);
					} catch (IOException e1) {
						logger.error("Error reading from clipboard.",e1);
					}
				}
				else if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {					
					try {
						String textValue = (String)contents.getTransferData(DataFlavor.stringFlavor);
						logger.debug("Simple string paste:"+textValue);
						int row=selectedRange.getFromRow();
						int col=selectedRange.getFromColumn();
						//Excel and OO put a tab seperated string on the clipboard if there are a number
						//of cells						
						StringTokenizer rowTokenizer=new StringTokenizer(textValue, "\n");
						while (rowTokenizer.hasMoreElements()) {
							String rowText=rowTokenizer.nextToken();							
							int startCol=col;
							String[] splitByTab=rowText.split("\t");
							for (String cellText : splitByTab) {								
								pasteTextValue(selectedRange.getSheet(),row,col, cellText.trim());
								col++;
							}						
							row++;
							col=startCol;
						}						
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

	private void pasteValidations(Range range,
			OntologyTermValidationDescriptor descriptor) {
		getWorkbookManager().removeValidations(range);
		if (descriptor!=null) {			
			getWorkbookManager().setValidationAt(range,descriptor.getType(), descriptor.getEntityIRI(),
					descriptor.getOWLPropertyItem(), (List<Term>) descriptor.getTerms());
		}
	}
	
	private void pasteTextValue(Sheet sheet,int row, int col, String textValue) {
		Range r = new Range(sheet, col, row, col, row);
		pasteTextValue(r, textValue);
	}

	private void pasteTextValue(Range selectedRange, String textValue) {
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
