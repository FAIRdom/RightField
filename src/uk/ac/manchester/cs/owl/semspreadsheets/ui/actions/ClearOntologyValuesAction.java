package uk.ac.manchester.cs.owl.semspreadsheets.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 20-Sep-2009
 */
public class ClearOntologyValuesAction extends WorkbookFrameAction {

    public ClearOntologyValuesAction(WorkbookFrame workbookFrame) {
        super("Clear ontology values", workbookFrame);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        int ret = JOptionPane.showConfirmDialog(getWorkbookFrame(), "Are you sure you want to clear all ontology term validation?", "Clear validation?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(ret == JOptionPane.YES_OPTION) {
            getWorkbookFrame().getWorkbookManager().getOntologyTermValidationManager().clearValidations();    
        }

    }
}
