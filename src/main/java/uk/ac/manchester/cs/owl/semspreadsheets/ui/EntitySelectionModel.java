package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public class EntitySelectionModel {

    private OWLEntity defaultSelection;

    private OWLEntity selectedEntity;
    
    private ValidationType validationType = ValidationType.NOVALIDATION;    

	private List<EntitySelectionModelListener> listeners = new ArrayList<EntitySelectionModelListener>();

    public EntitySelectionModel(OWLEntity defaultSelection) {
        this.defaultSelection = defaultSelection;
        selectedEntity = defaultSelection;        
    }

    public void setSelection(OWLEntity entity) {
        if(entity == null) {
            selectedEntity = defaultSelection;
            fireSelectionChange();
        }
        else {
            selectedEntity = entity;
            fireSelectionChange();
        }
    }
    
    public void clearSelection() {
        selectedEntity = defaultSelection;
        fireSelectionChange();
    }

    public OWLEntity getSelection() {
        return selectedEntity;
    }

    public void addListener(EntitySelectionModelListener listener) {
        if(listener == null) {
            throw new NullPointerException("Entity selection listener must not be null");
        }
        listeners.add(listener);
    }

    public void removeListener(EntitySelectionModelListener listener) {
        listeners.remove(listener);
    }

    protected void fireSelectionChange() {
        for(EntitySelectionModelListener listener : new ArrayList<EntitySelectionModelListener>(listeners)) {
            try {
                listener.selectionChanged();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    
    public ValidationType getValidationType() {
		return validationType;
	}

	public void setValidationType(ValidationType validationType) {
		this.validationType = validationType;
	}
}
