package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class RegistrationAction extends WorkbookFrameAction {

private String REG_URI="http://www.rightfield.org.uk/registration";
	
	private static Logger logger = Logger.getLogger(OnlineHelpAction.class);
	
	public RegistrationAction(WorkbookFrame workbookFrame) {
		super("Registration and Feedback",workbookFrame);
		
	}		
	
	@Override
	public void actionPerformed(ActionEvent action) {
		String msg = "RightField is developed as part of a publically funded academic researc project.\n";
		msg+="Please help support future open source development of RightField by registering your interest and,\n";
		msg+="optionally, provide some details about how you use RightField. \nIt is also an oppurtunity to easily provide feedback and suggestions.\n\n";
		msg+="Please select OK to proceed to open the registration form in a browser.\n";
		msg+="If you wish to register later, you can register via the Help menu.";
		int ret = JOptionPane.showConfirmDialog(getWorkbookFrame(), msg,"Feedback and Registration",JOptionPane.OK_CANCEL_OPTION);
		if (ret==JOptionPane.OK_OPTION) {
			try {
				Desktop.getDesktop().browse(URI.create(REG_URI));
			} catch (IOException e) {
				logger.error("There was a problem opening the URI: " + REG_URI, e);
				ErrorHandler.getErrorHandler().handleError(e);
			}
		}		
	}

}
