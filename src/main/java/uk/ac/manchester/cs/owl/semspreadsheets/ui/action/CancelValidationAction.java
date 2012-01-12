package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

@SuppressWarnings("serial")
public class CancelValidationAction extends WorkbookAction {
	
	public CancelValidationAction(WorkbookManager workbookManager) {
		super("Cancel",workbookManager);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getWorkbookManager().cancelValidationChange();
	}

}
