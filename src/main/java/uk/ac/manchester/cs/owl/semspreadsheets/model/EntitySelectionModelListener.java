/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.OWLEntity;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public interface EntitySelectionModelListener {	
    
    void owlPropertyChanged(OWLPropertyItem item);
    
    void validationTypeChanged(ValidationType type);
    
    void selectedEntityChanged(OWLEntity entity);
}
