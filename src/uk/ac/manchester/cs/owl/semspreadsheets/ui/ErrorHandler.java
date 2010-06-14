package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
public class ErrorHandler {

    private static ErrorHandler instance = new ErrorHandler();

    private ErrorHandler() {
    }

    public static ErrorHandler getErrorHandler() {
        return instance;
    }

    public void handleError(Throwable throwable) {
        if(throwable instanceof OWLOntologyCreationException) {
            if(throwable instanceof OWLOntologyCreationIOException) {
                OWLOntologyCreationIOException e = (OWLOntologyCreationIOException) throwable;
                JOptionPane.showMessageDialog(null, e.getCause().getMessage(), "Could not load ontology", JOptionPane.ERROR_MESSAGE);
            }
            else if(throwable instanceof UnparsableOntologyException) {
                JOptionPane.showMessageDialog(null, "The ontology document appears to be an unsupported format or contains syntax errors", "Could not load ontology", JOptionPane.ERROR_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(null, throwable.getMessage(), "Could not load ontology", JOptionPane.ERROR_MESSAGE);
            }

        }
        else if(throwable instanceof UnknownHostException) {
            JOptionPane.showMessageDialog(null, "You are not connected to the internet.  Please check your network connection.", "Not connected to network", JOptionPane.ERROR_MESSAGE);
        }
        else if (throwable instanceof IOException) {
            JOptionPane.showMessageDialog(null, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        throwable.printStackTrace();
    }
}
