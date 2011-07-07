package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.Repository;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class BioPortalRepository implements Repository {
	
	private static final Logger logger = Logger.getLogger(BioPortalRepository.class);

    public static final String NAME = "BioPortal";

    public static final String BASE = "http://rest.bioontology.org/bioportal/";

    public static final String ONTOLOGY_LIST = BASE + "ontologies/";

    public static final String ONTOLOGY_IRI_BASE = BASE + "ontologies/";

    private static String API_KEY = null;

    private Collection<RepositoryItem> repositoryItems = new ArrayList<RepositoryItem>();

    public BioPortalRepository() {
        OntologyListAccessor accessor = new OntologyListAccessor();
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
        if (strIri.contains(BioPortalRepository.BASE) && !strIri.contains("apikey")) {
        	//FIXME: need to join the parameter, as it may not be the only parameter
        	newIRI = IRI.create(strIri+"?apikey="+BioPortalRepository.readAPIKey());
        }
		return newIRI;
	}
	
	/**
	 * If appropriate, removes the appended apikey=XXX-XXX and returns a cleaned IRI
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
