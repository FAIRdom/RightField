package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

@SuppressWarnings("serial")
public class ApplyValidationAction extends WorkbookAction {

	public ApplyValidationAction(WorkbookManager workbookManager) {
		super("Apply",workbookManager);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		getWorkbookManager().applyValidationChange();		
	}

}
