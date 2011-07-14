package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SelectedCellsAction;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
@SuppressWarnings("serial")
public class AddValidationTest extends SelectedCellsAction {

    public AddValidationTest(WorkbookManager workbookManager) {
        super("Test validation", workbookManager);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        WorkbookManager wm = getWorkbookManager();
        final Sheet sheet = wm.getWorkbook().getSheets().iterator().next();
        Workbook wb = wm.getWorkbook();
        final Range range = getSelectedRange();
        wb.addName("test", range);
        sheet.clearValidation();
        sheet.addValidation("test", 0, 0, 0, 0);
    }
}
