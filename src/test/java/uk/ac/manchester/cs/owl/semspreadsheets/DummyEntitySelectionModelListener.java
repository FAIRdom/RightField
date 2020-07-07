package uk.ac.manchester.cs.owl.semspreadsheets;

import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.EntitySelectionModelListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Term;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;

import java.util.List;

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


	public boolean isTermsChanged() { return termsChanged; }


	public OWLPropertyItem getSelectedProperty() {
		return selectedProperty;
	}


	public OWLEntity getSelectedEntity() {
		return selectedEntity;
	}


	public ValidationType getSelectedValidationType() {
		return selectedValidationType;
	}


	public List<Term> getSelectedTerms() { return selectedTerms; }


	public DummyEntitySelectionModelListener() {
		reset();
	}

	private boolean owlPropertyChanged;
	private boolean validationTypeChanged;
	private boolean selectedEntityChanged;
	private boolean termsChanged;
	
	private OWLPropertyItem selectedProperty;
	private OWLEntity selectedEntity;
	private ValidationType selectedValidationType;
	private List<Term> selectedTerms;
	
	public void reset() {
		owlPropertyChanged=false;
		validationTypeChanged=false;
		selectedEntityChanged=false;
		termsChanged=false;
		selectedProperty=null;
		selectedEntity=null;
		selectedValidationType=null;
		selectedTerms=null;
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

	@Override
	public void termsChanged(List<Term> terms) {
		selectedTerms=terms;
		termsChanged=true;
	}

}
