package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

@SuppressWarnings("serial")
public class SheetCopyAction extends SelectedCellsAction {
	
	private static Logger logger = Logger.getLogger(SheetCopyAction.class);
	private final Toolkit toolkit;
	
	public SheetCopyAction(WorkbookManager workbookManager,Toolkit toolkit) {
		super("Copy", workbookManager);		
		this.toolkit = toolkit;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug("Copy action invoked");
		Range selectedRange = getSelectedRange();
		if (selectedRange.isCellSelection()) {
			if (selectedRange.isSingleCellSelected()) {
				int row=selectedRange.getFromRow();
				int col=selectedRange.getFromColumn();
				Cell cell = selectedRange.getSheet().getCellAt(col, row);
				String value="";
				if (cell!=null) {
					value = cell.getValue();					
				}
				else {
					logger.debug("Selected cell is returned as NULL");
					//we assume that we are copying an empty value, rather than leaving the clipboard intact
					value=""; 
				}
				Clipboard clippy = toolkit.getSystemClipboard();			
				clippy.setContents(new StringSelection(value), null);
				
				
			}
			else {
				logger.info("Copying a range of cells is not yet supported");
			}			
		}
		else {
			logger.info("Nothing selected");
		}
		
	}

}
