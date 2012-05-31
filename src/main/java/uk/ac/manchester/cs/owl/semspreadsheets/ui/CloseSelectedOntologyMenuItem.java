/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.Collection;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * A menu item that listens to OntologyValidation changes in the sheet, and tracks the selected ontology, to enable it to determine
 * its own enabled state. The selectedOntology is passed via the {@link WorkbookFrame} as it changes.
 * 
 * @author Stuart Owen
 *
 */
@SuppressWarnings("serial")
public class CloseSelectedOntologyMenuItem extends JMenuItem {

	private final WorkbookManager workbookManager;
	private OWLOntology selectedOntology;

	public CloseSelectedOntologyMenuItem(Action action, WorkbookManager workbookManager) {
		super(action);
		this.workbookManager = workbookManager;		
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
			Collection<IRI> ontologyIRIs = getWorkbookManager().getOntologyTermValidationManager().getOntologyIRIs();
			setEnabled(!ontologyIRIs.contains(selectedOntology.getOntologyID().getOntologyIRI()));
		}
	}

	protected WorkbookManager getWorkbookManager() {
		return workbookManager;
	}
	
	private void addListener() {
		getWorkbookManager().getOntologyTermValidationManager().addListener(new OntologyTermValidationListener() {
			
			@Override
			public void validationsChanged() {
				checkEnabledState();
				
			}
			
			@Override
			public void ontologyTermSelected(List<OntologyTermValidation> previewList) {
				
			}
		});
	}

}
