/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLEntity;


/**
 * Model containing the current selected validation type, ontology term, property items. This is the information that is stored in
 * the {@link OntologyTermValidationManager} when the change is applied.
 * 
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public class EntitySelectionModel {

    private OWLEntity defaultSelection;

    private OWLEntity selectedEntity;
    
    private ValidationType validationType = ValidationType.FREETEXT; 
    
    private OWLPropertyItem owlPropertyItem;
    
    private boolean allowSelectionEvents = true;

	private List<EntitySelectionModelListener> listeners = new ArrayList<EntitySelectionModelListener>();		

    public EntitySelectionModel(OWLEntity defaultSelection) {
        this.defaultSelection = defaultSelection;
        selectedEntity = defaultSelection;        
    }

    /**
     * clears the selection back to the defaultSelection, no property and free text
     */
    public synchronized void clear() {    	
    	allowSelectionEvents=false;
		setValidationType(ValidationType.FREETEXT);
		setOWLPropertyItem(null);
		setSelection(null);
		allowSelectionEvents=true;
		fireSelectionChange();
    }
    
    public void setSelection(OWLEntity entity) {
        if(entity == null) {
            selectedEntity = defaultSelection;            
        }
        else {
            selectedEntity = entity;            
        }
        fireSelectionChange();
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
    	if (allowSelectionEvents) {
    		for(EntitySelectionModelListener listener : new ArrayList<EntitySelectionModelListener>(listeners)) {
                try {
                    listener.selectionChanged();
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }
            }
    	}        
    }
    
    public ValidationType getValidationType() {
		return validationType;
	}

	public void setValidationType(ValidationType validationType) {
		this.validationType = validationType;
		fireSelectionChange();
	}

	public OWLPropertyItem getOWLPropertyItem() {
		return owlPropertyItem;
	}

	public void setOWLPropertyItem(OWLPropertyItem owlPropertyItem) {
		this.owlPropertyItem = owlPropertyItem;
		fireSelectionChange();
	}
}
