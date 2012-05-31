/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.repository;

import java.util.Collection;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public interface Repository {

    /**
     * Gets the name of the repository e.g. BioPortal, TONES
     * @return The name of the ontology
     */
    String getName();

    Collection<RepositoryItem> getOntologies();

    
}
