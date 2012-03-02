package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public interface OntologyTermValidationListener {

    void validationsChanged();

	void ontologyTermSelected(List<OntologyTermValidation> previewList);
}
