/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.listeners;

import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Term;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;

import java.util.List;

public abstract class AbstractEntitySelectionModelListener implements
		EntitySelectionModelListener {

	@Override
	public void owlPropertyChanged(OWLPropertyItem item) {
		
	}

	@Override
	public void validationTypeChanged(ValidationType type) {
		
	}

	@Override
	public void selectedEntityChanged(OWLEntity entity) {
		
	}

	@Override
	public void termsChanged(List<Term> terms) {

	}
}
