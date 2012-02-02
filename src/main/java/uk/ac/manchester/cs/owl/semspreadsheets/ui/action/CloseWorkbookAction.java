package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class CloseWorkbookAction extends WorkbookFrameAction {
	
	public CloseWorkbookAction(WorkbookFrame workbookFrame) {
		super("Close spreadsheet", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_W);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		getWorkbookFrame().closeWorkbook();
	}

}
