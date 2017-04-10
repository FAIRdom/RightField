/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
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
	
	private static final List<String> FORMAT_WHITE_LIST = Arrays.asList(new String []{"OWL","OBO","OWL-FULL","OWL-DL"});
		
    private String acronym;

    private String humanReadableName;

    private String format;
    
    private final BioPortalRepositoryAccessor repositoryAccessor;
    
    Logger logger = Logger.getLogger(BioPortalRepositoryItem.class);
	
    
    public BioPortalRepositoryItem(String acronym, String humanReadableName,BioPortalRepositoryAccessor repositoryAccessor) {
        this.acronym = acronym;
        this.humanReadableName = humanReadableName;
		this.repositoryAccessor = repositoryAccessor;
        this.format=null;
        
    }

    public String getHumanReadableName() {
        return humanReadableName;
    }
    
    public String getFormat() {
    	if (format==null) {
    		format=fetchFormat();
    	}
    	return format;
    }
    
    public boolean isCompatible() {
    	return FORMAT_WHITE_LIST.contains(getFormat());
    }
    
    private String fetchFormat() {
    	String result = null;
    	int count = 0;
    	int maxTries = 20;
    	while(true) {
    	    try {
    	    	result = repositoryAccessor.fetchOntologyFormat(acronym);
    	    	return result;
    	    } catch (IOException e) {
    	    	logger.error(acronym + " failed " + count);
    	        // handle exception
    	        if (++count == maxTries) {
    	        	logger.error(e);
    	        	return null;
    	        }
    	    }
    	}
    }

    public IRI getOntologyIRI() {
        return IRI.create(BioPortalRepository.ONTOLOGY_LIST + "/" + acronym + "/download" + "?apikey=" + BioPortalRepository.readAPIKey());
    }

    public IRI getVersionIRI() {
        return getOntologyIRI();
    }

    public IRI getPhysicalIRI() {
        return getOntologyIRI();
    }
    
    public String toString() {
    	return humanReadableName+" : "+acronym+" ("+format+")";    	
    }
    
}
