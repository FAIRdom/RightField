/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.export.Exporter;
import uk.ac.manchester.cs.owl.semspreadsheets.export.RDFExporter;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.RDFExportResultPanel;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class ExportRDFAction extends WorkbookFrameAction {
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ExportRDFAction.class);

	public ExportRDFAction(WorkbookFrame workbookFrame) {
		super("Export as RDF...",workbookFrame);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		IRI id=determineRootID();
		if (id!=null) {
			Exporter exporter = new RDFExporter(getWorkbookManager(), id);
			String rdf = exporter.export();
			
			RDFExportResultPanel.showDialog(getWorkbookFrame(), rdf);
		}
	}
	
	/**
	 * Determines the root ID for the top level RDF, i.e an identifier for the data file.
	 * Currently just asks the user for it.
	 * @return the root ID
	 */
	private IRI determineRootID() {
		String input = JOptionPane.showInputDialog(getWorkbookFrame(), "Please provide an identifier for this spreadsheet. It must be a valid URI");
		if (input!=null) {
			if (!validURI(input)) {
				JOptionPane.showMessageDialog(getWorkbookFrame(), "'" + input + "' is not a valid URI","Invalid URI",JOptionPane.ERROR_MESSAGE);
				return determineRootID();
			}
		}
		return IRI.create(input);
	}
	
	private boolean validURI(String str) {
		try {
			//FIMXE: just a short term check. doesn't seem to be an easy way to test a URI in java (URISyntaxException doesn't seem to be thrown.
			URI uri = new URI(str);
			if (uri.getScheme()==null) {
				return false;
			}
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
	}

}
