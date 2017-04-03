/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.AboutBoxPanel;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Action to display the {@link AboutBoxPanel}
 * 
 * @author Stuart Owen
 *
 */

@SuppressWarnings("serial")
public class AboutBoxAction extends WorkbookFrameAction {
	
	public AboutBoxAction(WorkbookFrame workbookFrame) {
		super("About", workbookFrame); 
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		AboutBoxPanel panel = new AboutBoxPanel();
		JOptionPane op = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.DEFAULT_OPTION);
		
		op.setOptions(new Object[]{"OK"});
		JDialog dialog = op.createDialog(getWorkbookFrame(),"About RightField");
		dialog.setVisible(true);
	}
}
