package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 10-Nov-2009
 * 
 * Author: Stuart Owen
 * Date: 04-Oct-2010
 */
@SuppressWarnings("serial")
public class RenameSheetAction extends WorkbookFrameAction {

    public RenameSheetAction(WorkbookFrame workbookFrame) {
        super("Rename", workbookFrame);
    }

    public void actionPerformed(ActionEvent e) {
        WorkbookFrame frame = getWorkbookFrame();
        Range range = frame.getWorkbookManager().getSelectionModel().getSelectedRange();
        Sheet sheet = range.getSheet();
        if(sheet != null) {
            String newName = JOptionPane.showInputDialog(frame, "Rename sheet to", sheet.getName());
            if (newName != null) {
            	if (getWorkbookManager().getWorkbook().getSheet(newName)==null) {
            		try {
                		getWorkbookManager().renameSheet(sheet.getName(),newName);
                	}
                	catch(IllegalArgumentException ex) {
                		ErrorHandler.getErrorHandler().handleError(ex);
                	}
            	}
            	else {
            		JOptionPane.showMessageDialog(getWorkbookFrame(), "A sheet with the name "+newName+" already exists","Duplicate sheet name",JOptionPane.ERROR_MESSAGE);
            	}            	
            }
        }
    }
}
