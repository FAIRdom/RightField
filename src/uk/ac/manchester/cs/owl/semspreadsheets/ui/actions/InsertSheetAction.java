package uk.ac.manchester.cs.owl.semspreadsheets.ui.actions;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 20-Sep-2009
 */
public class InsertSheetAction extends SpreadSheetAction {

    public InsertSheetAction(String name, WorkbookManager workbookManager, WorkbookFrame workbookFrame) {
        super(name, workbookManager, workbookFrame);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        Sheet sheet = getSpreadSheetFrame().addSheet();
        getSpreadSheetFrame().setSelectedSheet(sheet);
    }
}
