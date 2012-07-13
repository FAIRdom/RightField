/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.AbstractOntologyTermValidationListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;

/**
 * A menu item that listens to OntologyValidation changes in the sheet, and tracks the selected ontology, to enable it to determine
 * its own enabled state. The selectedOntology is passed via the {@link WorkbookFrame} as it changes.
 * 
 * @author Stuart Owen
 *
 */
@SuppressWarnings("serial")
public class CloseSelectedOntologyMenuItem extends JMenuItem {

	private OWLOntology selectedOntology;
	private final OntologyManager ontologyManager;

	public CloseSelectedOntologyMenuItem(Action action, OntologyManager ontologyManager) {
		super(action);
		this.ontologyManager = ontologyManager;		
		addListener();
	}
	
	public void setSelectedOntology(OWLOntology ontology) {
		selectedOntology=ontology;
		checkEnabledState();
	}
	
	private void checkEnabledState() {
		if (selectedOntology==null) {
			setEnabled(false);
		}
		else {			
			setEnabled(!getOntologyManager().isOntologyInUse(selectedOntology));
		}
	}	
	
	protected OntologyManager getOntologyManager() {
		return ontologyManager;
	}
			
	
	private void addListener() {
		getOntologyManager().addListener(new AbstractOntologyTermValidationListener() {			
			@Override
			public void validationsChanged() {
				checkEnabledState();
				
			}			
		});				
	}

}
