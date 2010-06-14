package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 20-Sep-2009
 */
public class InsertOntologyValuesAction extends SpreadSheetAction {

    public InsertOntologyValuesAction(WorkbookManager workbookManager, WorkbookFrame workbookFrame) {
        super("Ontology values", workbookManager, workbookFrame);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        Range range = getSpreadSheetManager().getSelectionModel().getSelectedRange();
        if(range == null) {
            return;
        }
        if(!range.isCellSelection()) {
            return;
        }
//        getSpreadSheetManager().addValidationRange(range);
        getSpreadSheetFrame().repaint();
    }
}
