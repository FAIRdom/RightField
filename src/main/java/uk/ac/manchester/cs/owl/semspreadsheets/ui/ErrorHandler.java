/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.model.InvalidWorkbookFormatException;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalAccessDeniedException;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
public class ErrorHandler {

    private static ErrorHandler instance = new ErrorHandler();
    
    private static Logger logger = Logger.getLogger(ErrorHandler.class);

    private ErrorHandler() {
    }

    public static ErrorHandler getErrorHandler() {
        return instance;
    }

    public void handleError(Throwable throwable) {
    	logger.debug("Error being handled",throwable);
        if(throwable instanceof OWLOntologyCreationException) {        	
            if(throwable instanceof OWLOntologyCreationIOException) {
                OWLOntologyCreationIOException e = (OWLOntologyCreationIOException) throwable;
                reportError("An error occurred trying to open the onology:"+e.getCause().getMessage(), "Could not open ontology",throwable);
            }
            else if(throwable instanceof UnparsableOntologyException) {
            	reportError("The ontology document appears to be in an unsupported format or contains syntax errors", "Could not load ontology",throwable);
            }
            else {
            	reportError(throwable.getMessage(), "Could not load ontology",throwable);
            }
        }
        else if(throwable instanceof UnknownHostException) {
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
        	JOptionPane.showMessageDialog(null, "An Unexpected "+throwable.getClass().getSimpleName() +" error occurred: " + throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        	logger.error("Unexpected error reported",throwable);
        }        
    }
    
    private void reportError(String message, String title,Throwable exception) {
    	JOptionPane.showMessageDialog(null, message,title,JOptionPane.ERROR_MESSAGE);
    }
}
