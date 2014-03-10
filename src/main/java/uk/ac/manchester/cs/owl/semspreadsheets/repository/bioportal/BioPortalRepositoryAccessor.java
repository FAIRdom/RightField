/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.Repository;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryAccessor;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public class BioPortalRepositoryAccessor implements RepositoryAccessor {

	private static Logger logger = Logger.getLogger(BioPortalRepositoryAccessor.class);
	
	private final int CONNECT_TIMEOUT = 30000;
	
	private final boolean SAVE_PROPERTIES_CACHE = false;
	
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
       
    public String fetchOntologyFormat(String ontologyAcronym) {
    	logger.debug("Fetching format for "+ontologyAcronym);
    	BioPortalCache cache = BioPortalCache.getInstance();
    	String format = cache.getFormat(ontologyAcronym);
    	if (format!=null) {
    		if (logger.isDebugEnabled()) {
    			logger.debug("The ontology format for "+ontologyAcronym+" was found in the cache as "+format);
    		}
    	}
    	else{
    		logger.info("The format for "+ontologyAcronym+" not found in cache, fetching");
    		try {
        		URL url = new URL(BioPortalRepository.ONTOLOGY_LIST+"/"+ontologyAcronym+"/"+BioPortalRepository.LATEST_SUBMISSION +"?format=json&apikey=" + BioPortalRepository.readAPIKey());
        		logger.info("About to fetch more ontology information from "+url.toExternalForm());
        		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        		connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(CONNECT_TIMEOUT);
                int responseCode = connection.getResponseCode();
                logger.info("BioPortal http response: " + responseCode);
                
                if (responseCode == 400 || responseCode == 403) {
                	throw new BioPortalAccessDeniedException();
                }
                
                JsonParser parser = new JsonFactory().createParser(connection.getInputStream());
                while (format==null && parser.nextToken()!=null) {
                	String name = parser.getCurrentName();            	
                	if ("hasOntologyLanguage".equals(name)) {
                		parser.nextToken();
                		format = parser.getText();                		                	
                	}
                } 
                if (format==null) {
                	format="unknown";
                }
                cache.storeFormat(ontologyAcronym, format);
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
    	}
    	 
    	return format;
    }     
    
    public Collection<RepositoryItem> getOntologies() {
        final Collection<RepositoryItem> items = new ArrayList<RepositoryItem>();
        URL url = null;
        try {
        	        	                    
            url = new URL(BioPortalRepository.ONTOLOGY_LIST + "?format=json&apikey=" + BioPortalRepository.readAPIKey());        	
            
            logger.debug("Contacting BioPortal REST API at: "+url.toExternalForm());
                                   
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
            	            	
        		BioPortalRepositoryItem repositoryItem = new BioPortalRepositoryItem(id,name,this);        		
    			if (repositoryItem.isCompatible()) {
    				items.add(repositoryItem);
    			}        	
            }
            if (SAVE_PROPERTIES_CACHE) {
            	BioPortalCache.getInstance().dumpStoredProperties();
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
