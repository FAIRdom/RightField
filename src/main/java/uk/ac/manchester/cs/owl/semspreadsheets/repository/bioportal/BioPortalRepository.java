/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.Repository;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryManager;

/**
 * 
 * @author Stuart Owen
 * @author Matthew Horridge
**/

public class BioPortalRepository implements Repository {
	
	private static final Logger logger = Logger.getLogger(BioPortalRepository.class);

    public static final String NAME = "BioPortal";

    public static final String BASE = "http://data.bioontology.org/";
    
    public static final String OLD_BASE = "http://rest.bioontology.org/";

    public static final String ONTOLOGY_LIST = BASE + "ontologies";
    
    public static final String ONTOLOGY = BASE + "ontology";
    
    public static final String LATEST_SUBMISSION = "latest_submission";

    private static String API_KEY = null;

    private Collection<RepositoryItem> repositoryItems = new ArrayList<RepositoryItem>();

    public BioPortalRepository() {
    	BioPortalRepositoryAccessor accessor = RepositoryManager.getInstance().getBioPortalRepositoryAccessor();
        repositoryItems.addAll(accessor.getOntologies());
    }

    public String getName() {
        return NAME;
    }

    public Collection<RepositoryItem> getOntologies() {
        return repositoryItems;
    }
    
    public static String readAPIKey() {
    	URL resource;    	
    	if (API_KEY == null) {
	    	resource = BioPortalRepository.class.getResource("/bioportal_api_key");
	    	if (resource != null) {
	    		char [] buffer = new char[1024];
	        	try {
	        		InputStreamReader reader = new InputStreamReader(resource.openStream());
	    			reader.read(buffer);
	    			API_KEY = String.valueOf(buffer).trim();	    			
	    		} catch (IOException e) {
	    			logger.error("Error reading bioportal_api_key",e);
	    		}
	    	}
	    	else {
	    		logger.error("Unable to determine the API Key for BioPortal. The file bioportal_api_key file is missing");
	    		API_KEY="unknown";
	    	}
    	}    	
		return API_KEY;
    }
    
    /**
     * Determines whether the IRI needs the BioPortal api key appending, and if so does so and returns the correct IRI
     * @param iri
     * @return the iri with the api key appended if necessary
     */
	public static IRI handleBioPortalAPIKey(IRI iri) {
		IRI newIRI = iri;
		String strIri=iri.toString();
        if ((strIri.contains(BioPortalRepository.OLD_BASE) || strIri.contains(BioPortalRepository.BASE) && !strIri.contains("apikey"))) {
        	strIri=LegacyBioportalSourceResolver.getInstance().resolve(iri).toString();
        	//FIXME: need to join the parameter, as it may not be the only parameter
        	newIRI = IRI.create(strIri+"?apikey="+BioPortalRepository.readAPIKey());
        }
		return newIRI;
	}
	
	/**
	 * If appropriate, removes the appended apikey=some_value
	 */
	public static IRI removeBioPortalAPIKey(IRI iri) {
		IRI newIRI = iri;
		String strIri=iri.toString();
        if (strIri.contains(BioPortalRepository.BASE) && strIri.contains("apikey")) {
        	//FIXME: this assumes that apikey is the last parameter - which currently is always the case but may not always be
        	int i=strIri.indexOf("?apikey");
        	strIri=strIri.substring(0,i);
        	newIRI = IRI.create(strIri);
        }
		return newIRI;
	}
    
}
