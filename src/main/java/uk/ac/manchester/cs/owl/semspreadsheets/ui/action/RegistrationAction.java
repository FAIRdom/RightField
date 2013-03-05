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
		String msg = "Registration of RightField is enitirely optional,\nbut doing so helps provide the continued development and support of RightField. \nIt is also an oppurtunity to easily provide feedback and suggestions.";
		msg+="\n\nPlease select OK to proceed to open the registration form in a browser.";
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
