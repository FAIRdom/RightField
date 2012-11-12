/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class RDFExportResultPanel extends JPanel {
	
	private final static Logger logger = Logger.getLogger(RDFExportResultPanel.class);
		
	private JTextArea rdfTextArea;

	private final WorkbookFrame workbookFrame;

	private final JDialog parent;

	public RDFExportResultPanel(JDialog parent,WorkbookFrame workbookFrame,final String rdf) {
		super();
		this.parent = parent;
		this.workbookFrame = workbookFrame;
		setPreferredSize(new Dimension(700,400));
		
		rdfTextArea = new JTextArea();
		rdfTextArea.setEditable(false);
		rdfTextArea.setText(rdf);						
		rdfTextArea.setCaretPosition(0);		
		JScrollPane scrollPane = new JScrollPane(rdfTextArea);
		
		setLayout(new BorderLayout());		
		add(scrollPane,BorderLayout.CENTER);		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton closeButton = new JButton(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();				
			}
		});
		
		JButton saveButton = new JButton(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				save(rdf);			
			}
		});
		saveButton.setText("Save");
		closeButton.setText("Close");
		buttonPanel.add(saveButton);
		buttonPanel.add(closeButton);		
		add(buttonPanel,BorderLayout.SOUTH);				
	}
	
	private WorkbookFrame getWorkbookFrame() {
		return workbookFrame;
	}
	
	private void close() {
		parent.dispose();
	}
	
	private void save(String rdf) {
		//FIXME: workbookFrame is not a suitable place for these file based methods, but have already been refactored
		//to another class in the xlsx branch.
		File file = getWorkbookFrame().browseForFile("Save RDF as", FileDialog.SAVE,
				"RDF File",new String [] {"rdf"});	
		if (file!=null) {
			file = getWorkbookFrame().checkForDefaultExtension(file, ".rdf");
			PrintWriter writer;
			try {
				writer = new PrintWriter(file);
				writer.write(rdf);
				writer.close();
			} catch (FileNotFoundException e) {
				ErrorHandler.getErrorHandler().handleError(e);
			}
		}					
	}
	
	public static void showDialog(WorkbookFrame frame,String rdf) {
		logger.debug("About to show dialog for RDF export");
		logger.debug(rdf);
		JDialog dialog = new JDialog(frame,"Generated RDF");
		
		RDFExportResultPanel panel = new RDFExportResultPanel(dialog,frame,rdf);		
		dialog.add(panel);
		dialog.pack();
		dialog.setVisible(true);
	}
	
}
