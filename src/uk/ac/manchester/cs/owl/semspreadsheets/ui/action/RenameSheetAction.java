package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 10-Nov-2009
 */
public class RenameSheetAction extends WorkbookFrameAction {

    public RenameSheetAction(WorkbookFrame workbookFrame) {
        super("Rename", workbookFrame);
    }

    public void actionPerformed(ActionEvent e) {
        WorkbookFrame frame = getWorkbookFrame();
        Range range = frame.getWorkbookManager().getSelectionModel().getSelectedRange();
        Sheet sheet = range.getSheet();
        if(sheet != null) {
            String name = JOptionPane.showInputDialog(frame, "Rename sheet to", sheet.getName());
            if (name != null) {
                sheet.setName(name);
            }
        }
    }
}
