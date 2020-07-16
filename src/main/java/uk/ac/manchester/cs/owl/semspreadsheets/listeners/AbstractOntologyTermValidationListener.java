/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.listeners;

import java.util.List;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;

public abstract class AbstractOntologyTermValidationListener implements
		OntologyTermValidationListener {

	@Override
	public void validationsChanged() {

	}

	@Override
	public void ontologyTermSelected(List<OntologyTermValidation> previewList) {		

	}

}
