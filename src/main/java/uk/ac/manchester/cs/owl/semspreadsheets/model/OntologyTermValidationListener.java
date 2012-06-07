/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.List;

/**
 * @author Matthew Horridge
 */
public interface OntologyTermValidationListener {

    void validationsChanged();

	void ontologyTermSelected(List<OntologyTermValidation> previewList);
}
