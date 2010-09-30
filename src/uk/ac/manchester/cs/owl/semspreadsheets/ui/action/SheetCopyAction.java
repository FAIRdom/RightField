package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

@SuppressWarnings("serial")
public class SheetCopyAction extends SelectedCellsAction {
	
	public SheetCopyAction(WorkbookManager workbookManager) {
		super("Copy", workbookManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("COPY CALLED");
	}

}
