/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.Repository;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryAccessor;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public class BioPortalRepositoryAccessor implements RepositoryAccessor {

	private static Logger logger = Logger.getLogger(BioPortalRepositoryAccessor.class);
	
	private final int CONNECT_TIMEOUT = 30000;
	
    private BioPortalRepository repository;

    public String getRepositoryName() {
        return BioPortalRepository.NAME;
    }

    public synchronized Repository getRepository() {
        if(repository == null) {
            repository = new BioPortalRepository();
        }
        return repository;
    }
    
    public static void main(String [] args) throws Exception {
    	new BioPortalRepositoryAccessor().getOntologies();
    }
    
    public Collection<RepositoryItem> getOntologies() {
        final Collection<RepositoryItem> items = new ArrayList<RepositoryItem>();
        URL url = null;
        try {
                     
            url = new URL(BioPortalRepository.ONTOLOGY_LIST + "?format=json&apikey=" + BioPortalRepository.readAPIKey());        	
            
            logger.info("Contacting BioPortal REST API at: "+url.toExternalForm());
                                   
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();            
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(CONNECT_TIMEOUT);
            int responseCode = connection.getResponseCode();
            logger.info("BioPortal http response: " + responseCode);
            
            if (responseCode == 400 || responseCode == 403) {
            	throw new BioPortalAccessDeniedException();
            }            
            ObjectMapper mapper = new ObjectMapper();            
            
            JsonNode node = mapper.readTree(connection.getInputStream());
            for (final JsonNode item : node) {
            	String name = item.get("name").asText();
            	String id = item.get("acronym").asText();
            	if (logger.isDebugEnabled()) {
            		logger.debug("Found BioPortal ontology: " + name +" acronym:"+id);
            	}
            	if (items.size()<10) {
            		items.add(new BioPortalRepositoryItem(id,name));
            	}
            }

        }        
        catch (UnknownHostException e) {
            ErrorHandler.getErrorHandler().handleError(e);
        }
        catch (MalformedURLException e) {
            logger.error("Error with URL for BioPortal rest API",e);
        }
        catch (SocketTimeoutException e) {
        	logger.error("Timeout connecting to BioPortal",e);
        	ErrorHandler.getErrorHandler().handleError(e);
        }
        catch (IOException e) {        	
            logger.error("Error communiciating with BioPortal rest API",e);                    	
        }
        catch (BioPortalAccessDeniedException e) {
        	ErrorHandler.getErrorHandler().handleError(e);
        }        
        return items;
    }
}
