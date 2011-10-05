package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class OnlineHelpAction extends WorkbookFrameAction {

	private String HELP_URI="http://rightfield.org.uk/guide";
	
	private Logger logger = Logger.getLogger(OnlineHelpAction.class);
	
	public OnlineHelpAction(WorkbookFrame workbookFrame) {
		super("Online Help",workbookFrame);
		
	}		
	
	@Override
	public void actionPerformed(ActionEvent action) {
		try {
			Desktop.getDesktop().browse(URI.create(HELP_URI));
		} catch (IOException e) {
			logger.error("There was a problem opening the URI: " + HELP_URI, e);
			ErrorHandler.getErrorHandler().handleError(e);
		}
	}

}
