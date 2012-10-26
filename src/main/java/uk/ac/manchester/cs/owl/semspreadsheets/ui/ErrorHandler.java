/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.model.InvalidWorkbookFormatException;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalAccessDeniedException;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepository;
import uk.org.rightfield.RightField;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class ErrorHandler {

    private static ErrorHandler instance = new ErrorHandler();
    
    private static Logger logger = Logger.getLogger(ErrorHandler.class);

    private ErrorHandler() {
    }

    public static ErrorHandler getErrorHandler() {
        return instance;
    }

    public void handleError(OWLOntologyCreationException throwable, IRI iri) {
    	iri = BioPortalRepository.removeBioPortalAPIKey(iri);
    	logger.debug("Error being handled whilst ",throwable);
    	if(throwable instanceof OWLOntologyCreationIOException) {            
            if (throwable.getCause() instanceof UnknownHostException) {
            	reportError("Unable to determine the address whilst trying to open the ontology for "+iri.toString()+", it appears you are not connected to the internet", "Could not open ontology",throwable.getCause());
            }
            else {
            	reportError("There was a network related error trying to open the ontology for "+iri.toString()+", the resource you are trying to connect to may not be available or accessible", "Could not open ontology",throwable.getCause());
            }            
        }
        else if(throwable instanceof UnparsableOntologyException) {
        	reportError("The ontology document for " + iri.toString() + " appears to be in an unsupported format or contains syntax errors", "Could not load ontology",throwable.getCause());
        }
        else {
        	reportError("There was an error trying to open the ontology at "+iri.toString()+" : " + throwable.getMessage(), "Could not load ontology",throwable);
        }
    }
    
    public void handleError(Throwable throwable) {
    	logger.debug("Error being handled",throwable);
        if(throwable instanceof UnknownHostException) {
        	reportError("You are not connected to the internet.  Please check your network connection.", "Not connected to network",throwable);
        }        
        else if (throwable instanceof BioPortalAccessDeniedException) {
        	reportError("Access to the BioPortal API was forbidden. This could be due to an invalid API key.", "Error",throwable);
        }
        else if (throwable instanceof MalformedURLException || throwable instanceof URISyntaxException) {
        	reportError("The URL provided was invalid", "Error",throwable);
        }   
        else if (throwable instanceof InvalidWorkbookFormatException) {
        	reportError(throwable.getMessage(),"Invalid file format",throwable);
        }
        else {
        	reportError("An Unexpected "+throwable.getClass().getSimpleName() +" error occurred: " + throwable.getMessage(),"Error",throwable);        	
        	logger.error("Unexpected error reported",throwable);
        }        
    }
    
    private void reportError(String message, String title,Throwable exception) {
    	if (exception != null) {
    		title=title+" ("+exception.getClass().getSimpleName()+")";
    	}
    	
    	ErrorHandlingPanel errorHandlingPanel = new ErrorHandlingPanel(message, exception);
    	JOptionPane op = new JOptionPane(errorHandlingPanel, JOptionPane.ERROR_MESSAGE,
				JOptionPane.DEFAULT_OPTION);
		JDialog dlg = op.createDialog(errorHandlingPanel, title);
    	
		dlg.setVisible(true);		
    }
    
    @SuppressWarnings("serial")
	private class ErrorHandlingPanel extends JPanel {    	    	

		public ErrorHandlingPanel(String message, Throwable exception) {			
			
			setLayout(new BorderLayout());
			JPanel messagePanel = displayMessage(message);
			
			
			add(messagePanel,BorderLayout.NORTH);
						
			if (exception != null) {				
				JButton toggleGoryDetailsButton = displayGoryDetails(exception);
				messagePanel.add(toggleGoryDetailsButton);				
			}			
    	}

		private JButton displayGoryDetails(Throwable exception) {
			JTextArea stackTrace = new JTextArea();
			stackTrace.setEditable(false);
			String goryDetails = "These are technical details that can be sent to the RightField developers to help diagnose an unexpected problem";
			
			goryDetails += "\n\nThe version of RightField is: "+RightField.getApplicationVersion();
			goryDetails+="\n\n";
			goryDetails+=stackTraceAsString(exception);
			stackTrace.setText(goryDetails);
			
			final JScrollPane stackTracePane = new JScrollPane(stackTrace);				
			
			stackTracePane.setPreferredSize(new Dimension(550,250));
			stackTracePane.revalidate();
			
			stackTracePane.setVisible(false);
			
			add(stackTracePane,BorderLayout.CENTER);
			
			final JButton showTraceButton = new JButton("Gory details...");
			
			final JPanel  thisPanel = this;
			showTraceButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!stackTracePane.isVisible()) {
						showTraceButton.setText("Hide details...");
						stackTracePane.setVisible(true);
						SwingUtilities.getWindowAncestor(thisPanel).pack();														
					}
					else {
						showTraceButton.setText("Gory details...");
						stackTracePane.setVisible(false);
						SwingUtilities.getWindowAncestor(thisPanel).pack();
					}						
				}
			});
			stackTrace.setCaretPosition(0);
			return showTraceButton;
		}

		private JPanel displayMessage(String message) {
			JPanel messagePanel = new JPanel();
			JTextArea messageLabel = new JTextArea(message);
			messageLabel.setOpaque(false);
			messageLabel.setEditable(false);			
			messageLabel.setRows(2);
			messageLabel.setLineWrap(true);			
			messageLabel.setWrapStyleWord(true);
			Font font = messageLabel.getFont();
			Font bold = new Font(font.getName(),Font.BOLD,font.getSize());
			messageLabel.setFont(bold);
			
			messageLabel.setPreferredSize(new Dimension(550,60));					
			messagePanel.add(messageLabel);
			return messagePanel;
		}
		
		private String stackTraceAsString(Throwable throwable) {
			StringWriter sw = new StringWriter();
			throwable.printStackTrace(new PrintWriter(sw));
			return sw.toString();
		}
    }
    
    public static void main(String [] args) {
    	try {
    		throw new Exception("sd sdfsd fsdf sdfsdfsdf sdfsd sdf sdfsdf \nsdfsdf sdf sdf sdf frex.xml");
    	}
    	catch(Exception e) {
    		ErrorHandler.getErrorHandler().handleError(e);
    	}    	
    	System.exit(0);
    }
}
