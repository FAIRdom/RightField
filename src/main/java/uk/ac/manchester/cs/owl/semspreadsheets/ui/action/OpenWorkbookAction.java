package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
@SuppressWarnings("serial")
public class OpenWorkbookAction extends WorkbookFrameAction {

    public OpenWorkbookAction(WorkbookFrame workbookFrame) {
        super("Open workbook...", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_O);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        getWorkbookFrame().openWorkbook();        
    }
}
