package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JButton;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

@SuppressWarnings("serial")
public class ApplyValidationAction extends WorkbookAction {

	public ApplyValidationAction(WorkbookManager workbookManager) {
		super("Apply",workbookManager);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		getWorkbookManager().applyValidation();
		Object source = e.getSource();
		if (source!=null && source instanceof JButton) {
			((JButton)source).setEnabled(false);
		}
	}

	

}
