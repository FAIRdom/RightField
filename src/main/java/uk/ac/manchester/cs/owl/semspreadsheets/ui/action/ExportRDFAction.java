/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.export.Exporter;
import uk.ac.manchester.cs.owl.semspreadsheets.export.RDFExporter;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class ExportRDFAction extends WorkbookFrameAction {
	
	private static Logger logger = Logger.getLogger(ExportRDFAction.class);

	public ExportRDFAction(WorkbookFrame workbookFrame) {
		super("Export as RDF...",workbookFrame);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String id="http://df/data/1";
		Exporter exporter = new RDFExporter(getWorkbookManager(), id);
		String rdf = exporter.export();
		logger.debug("Generated RDF:");
		logger.debug(rdf);
		showRDF(rdf);
	}

	private void showRDF(String rdf) {
		JPanel panel = new JPanel();
		panel.add(new JTextArea(rdf));
		JDialog dialog = new JDialog();
		dialog.setTitle("Generated RDF");
		dialog.add(panel);
		dialog.pack();
		dialog.setVisible(true);
	}

}
