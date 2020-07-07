/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.EntitySelectionModelListener;


/**
 * Model containing the current selected validation type, ontology term, property items. This is the information that is stored in
 * the {@link OntologyTermValidationManager} when the change is applied.
 * 
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public class EntitySelectionModel {
		
	private static final Logger logger = Logger.getLogger(EntitySelectionModel.class);

    private OWLEntity defaultSelection;

    private OWLEntity selectedEntity;
    
    private ValidationType validationType = ValidationType.FREETEXT; 
    
    private OWLPropertyItem owlPropertyItem;

	private List<Term> terms;

	private boolean allowSelectionEvents = true;

	private List<EntitySelectionModelListener> listeners = new ArrayList<EntitySelectionModelListener>();		

    public EntitySelectionModel(OWLEntity defaultSelection) {
        this.defaultSelection = defaultSelection;
        selectedEntity = defaultSelection;        
    }    
    
    public void setSelectedEntity(OWLEntity entity) {    	
    	
    	OWLEntity oldEntity = this.selectedEntity;
        if(entity == null) {
            this.selectedEntity = defaultSelection;            
        }
        else {
            this.selectedEntity = entity;            
        }
        terms = null;
		logger.debug("Set selected entity to "+selectedEntity.getIRI().toString());
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
		logger.debug("Setting validation type to :"+validationType);
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
		this.terms = null;
		if (oldItem==null ? this.owlPropertyItem!=null : !oldItem.equals(this.owlPropertyItem)) {
			fireOWLPropertyChanged();			
		}		
	}

	public List<Term> getTerms() { return terms; }

	public void setTerms(List<Term> terms) {
		if (!Objects.equals(terms, this.terms)) {
			this.terms = terms;
			fireTermsChanged();
		}
	}
    
    public void clearSelection() {
    	logger.debug("Clearing selected Entity");
        setSelectedEntity(defaultSelection);
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

	protected void fireTermsChanged() {
		if (allowSelectionEvents) {
			for (EntitySelectionModelListener listener : new ArrayList<EntitySelectionModelListener>(
					listeners)) {
				listener.termsChanged(terms);
			}
		}
	}
}
