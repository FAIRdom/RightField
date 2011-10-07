package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
@SuppressWarnings("serial")
public class SaveAsAction extends WorkbookFrameAction {

    public SaveAsAction(WorkbookFrame workbookFrame) {
        super("Save spreadsheet as...", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_S, true, false);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        try {
            getWorkbookFrame().saveWorkbookAs();
        }
        catch (IOException e1) {
            ErrorHandler.getErrorHandler().handleError(e1);
        }
    }
}
