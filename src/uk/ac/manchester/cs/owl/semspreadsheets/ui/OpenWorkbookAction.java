package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
public class OpenWorkbookAction extends WorkbookFrameAction {

    public OpenWorkbookAction(WorkbookFrame workbookFrame) {
        super("Open workbook...", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_O);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        try {
            getWorkbookFrame().openWorkbook();
        }
        catch (IOException e1) {
            ErrorHandler.getErrorHandler().handleError(e1);
        }
    }
}
