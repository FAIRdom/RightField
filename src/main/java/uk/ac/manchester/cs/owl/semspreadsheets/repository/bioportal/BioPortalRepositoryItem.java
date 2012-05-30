/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009<br>
 * 
 * Author: Stuart Owen<br>
 * Date: 15-June-2010
 * 
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public class BioPortalRepositoryItem implements RepositoryItem {

    private int ontologyID;

    private String humanReadableName;

    private String format;
    
    public BioPortalRepositoryItem(int ontologyID, String humanReadableName,String format) {
        this.ontologyID = ontologyID;
        this.humanReadableName = humanReadableName;
        this.format=format;
    }

    public String getHumanReadableName() {
        return humanReadableName;
    }
    
    public String getFormat() {
    	return format;
    }

    public IRI getOntologyIRI() {
        return IRI.create(BioPortalRepository.BASE + "virtual/download/" + ontologyID);
    }

    public IRI getVersionIRI() {
        return getOntologyIRI();
    }

    public IRI getPhysicalIRI() {
        return getOntologyIRI();
    }
    
    public String toString() {
    	return humanReadableName+" : "+ontologyID+" ("+format+")";    	
    }
    
}
