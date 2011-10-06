package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class ExitAction extends WorkbookFrameAction {

	public ExitAction(WorkbookFrame frame) {
		super("Exit",frame);
	}
	@Override
	public void actionPerformed(ActionEvent action) {		
		getWorkbookFrame().exit();
	}

}
