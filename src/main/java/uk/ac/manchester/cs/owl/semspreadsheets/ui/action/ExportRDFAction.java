/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.export.Exporter;
import uk.ac.manchester.cs.owl.semspreadsheets.export.RDFExporter;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
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
		try {
			IRI iris[]=determineRootAndPropertyIDs();
			IRI rootID=iris[0];
			IRI propertyID=iris[1]!=null ? iris[1] : IRI.create(RDFExporter.DEFAULT_PROPERTY_URI);
			if (rootID!=null) {			
				Exporter exporter = new RDFExporter(getWorkbookManager(), rootID,propertyID);
				String rdf = exporter.export();
				
				RDFExportResultPanel.showDialog(getWorkbookFrame(), rdf);
			}
		}
		catch(Exception ex) {
			ErrorHandler.getErrorHandler().handleError(ex);
		}
	}
	
	/**
	 * Determines the root ID for the top level RDF, i.e an identifier for the data file, and also the default propertyID (which defaults to {@link RDFExporter#DEFAULT_PROPERTY_URI}
	 * Currently just asks the user for it.
	 * @return the [root ID, propertyID]
	 * 
	 */
	private IRI[] determineRootAndPropertyIDs() {
		IRI rootID = null;
		IRI propertyID = null;
		JTextField rootIDFIeld = new JTextField();
		JTextField propertyIDField = new JTextField(RDFExporter.DEFAULT_PROPERTY_URI+" ");
		JComponent [] components = new JComponent[] {
				new JLabel("Spreadsheet root identifier URI"),
				rootIDFIeld,
				new JLabel("Default property identifier URI"),
				propertyIDField
		};
		int retVal = 99;
		boolean validInput=false;
		while (!validInput) {
			retVal = JOptionPane.showConfirmDialog(getWorkbookFrame(), components,"Please provide some identifiers",JOptionPane.OK_CANCEL_OPTION);
			if (retVal == JOptionPane.OK_OPTION) {
				String propertyURI = propertyIDField.getText().trim();
				String rootURI = rootIDFIeld.getText().trim();
				if (!validURI(rootURI)) {
					JOptionPane.showMessageDialog(getWorkbookFrame(), "'"+rootURI+"' is not a valid URI");
				}
				else if (!validURI(propertyURI)) {
					JOptionPane.showMessageDialog(getWorkbookFrame(), "'"+propertyURI+"' is not a valid URI");
				}
				else {					
					rootID=IRI.create(rootURI);
					propertyID=IRI.create(propertyURI);
					validInput=true;
				}				
			}	
			else {
				//cancel is a valid input
				validInput=true;
			}
		}
		
		return new IRI[] {rootID,propertyID};
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
