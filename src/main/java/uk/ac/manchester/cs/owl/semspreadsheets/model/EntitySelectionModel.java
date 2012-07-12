/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLEntity;


/**
 * Model containing the current selected validation type, ontology term, property items. This is the information that is stored in
 * the {@link OntologyTermValidationManager} when the change is applied.
 * 
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public class EntitySelectionModel {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EntitySelectionModel.class);

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
		setSelectedEntity(null);
		allowSelectionEvents=true;		
    }
    
    public void setSelectedEntity(OWLEntity entity) {
    	OWLEntity oldEntity = this.selectedEntity;
        if(entity == null) {
            this.selectedEntity = defaultSelection;            
        }
        else {
            this.selectedEntity = entity;            
        }
        if (oldEntity==null ? selectedEntity!=null : !oldEntity.equals(selectedEntity)) {
        	fireSelectedEntityChanged();        	        
        }        
    }
    
    public ValidationType getValidationType() {
		return validationType;
	}

	public void setValidationType(ValidationType validationType) {
		ValidationType oldType = this.validationType;
		this.validationType = validationType;
		if (oldType==null ? this.validationType!=null : !oldType.equals(this.validationType)) {
			fireValidationTypeChanged();			
		}		
	}

	public OWLPropertyItem getOWLPropertyItem() {
		return owlPropertyItem;
	}

	public void setOWLPropertyItem(OWLPropertyItem owlPropertyItem) {
		OWLPropertyItem oldItem = this.owlPropertyItem;
		this.owlPropertyItem = owlPropertyItem;
		if (oldItem==null ? this.owlPropertyItem!=null : !oldItem.equals(this.owlPropertyItem)) {
			fireOWLPropertyChanged();			
		}		
	}
    
    public void clearSelection() {
        selectedEntity = defaultSelection;
        fireSelectedEntityChanged();
    }

    public OWLEntity getSelectedEntity() {
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
    
	protected void fireSelectedEntityChanged() {
		if (allowSelectionEvents) {
			for (EntitySelectionModelListener listener : new ArrayList<EntitySelectionModelListener>(
					listeners)) {
				listener.selectedEntityChanged(selectedEntity);
			}
		}
	}

	protected void fireOWLPropertyChanged() {
		if (allowSelectionEvents) {
			for (EntitySelectionModelListener listener : new ArrayList<EntitySelectionModelListener>(
					listeners)) {
				listener.owlPropertyChanged(owlPropertyItem);
			}
		}
	}

	protected void fireValidationTypeChanged() {
		if (allowSelectionEvents) {
			for (EntitySelectionModelListener listener : new ArrayList<EntitySelectionModelListener>(
					listeners)) {

				listener.validationTypeChanged(validationType);

			}
		}
	}
}
