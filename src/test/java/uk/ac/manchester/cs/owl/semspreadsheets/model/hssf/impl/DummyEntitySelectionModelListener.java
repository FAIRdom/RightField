package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.EntitySelectionModelListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;

public class DummyEntitySelectionModelListener implements EntitySelectionModelListener {

	public boolean isOwlPropertyChanged() {
		return owlPropertyChanged;
	}


	public boolean isValidationTypeChanged() {
		return validationTypeChanged;
	}


	public boolean isSelectedEntityChanged() {
		return selectedEntityChanged;
	}


	public OWLPropertyItem getSelectedProperty() {
		return selectedProperty;
	}


	public OWLEntity getSelectedEntity() {
		return selectedEntity;
	}


	public ValidationType getSelectedValidationType() {
		return selectedValidationType;
	}


	public DummyEntitySelectionModelListener() {
		reset();
	}

	private boolean owlPropertyChanged;
	private boolean validationTypeChanged;
	private boolean selectedEntityChanged;
	
	private OWLPropertyItem selectedProperty;
	private OWLEntity selectedEntity;
	private ValidationType selectedValidationType;
	
	public void reset() {
		owlPropertyChanged=false;
		validationTypeChanged=false;
		selectedEntityChanged=false;
		selectedProperty=null;
		selectedEntity=null;
		selectedValidationType=null;
	}
	
	
	@Override
	public void owlPropertyChanged(OWLPropertyItem item) {
		selectedProperty=item;
		owlPropertyChanged=true;
	}

	@Override
	public void validationTypeChanged(ValidationType type) {
		selectedValidationType=type;
		validationTypeChanged=true;				
	}

	@Override
	public void selectedEntityChanged(OWLEntity entity) {
		selectedEntity=entity;
		selectedEntityChanged=true;		
	}

}
