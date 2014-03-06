/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

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
	
	public static Properties bioportalCachedDetails = null;
	
	private final int CONNECT_TIMEOUT = 30000;

    private String acroynm;

    private String humanReadableName;

    private String format;
    
    Logger logger = Logger.getLogger(BioPortalRepositoryItem.class);
    
    public BioPortalRepositoryItem(String acroynm, String humanReadableName) {
        this.acroynm = acroynm;
        this.humanReadableName = humanReadableName;
        this.format=null;
        if (BioPortalRepositoryItem.bioportalCachedDetails==null) {
        	BioPortalRepositoryItem.bioportalCachedDetails = new Properties();
        	try {
        		InputStream stream = BioPortalRepositoryItem.class.getResource("/bioportal-cache.properties").openStream();
				BioPortalRepositoryItem.bioportalCachedDetails.load(stream);
				stream.close();
			} catch (IOException e) {
				logger.error("Error reading bioportal properties file",e);
			}
        }
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
    	logger.debug("Fetching format for "+acroynm);
    	String format = BioPortalRepositoryItem.bioportalCachedDetails.getProperty(acroynm);
    	if (format!=null) {
    		if (logger.isDebugEnabled()) {
    			logger.debug("The ontology format for "+acroynm+" was found in the cache as "+format);
    		}
    	}
    	else{
    		logger.info("The format for "+acroynm+" not found in cache, fetching");
    		try {
        		URL url = new URL(BioPortalRepository.ONTOLOGY_LIST+"/"+acroynm+"/"+BioPortalRepository.LATEST_SUBMISSION +"?format=json&apikey=" + BioPortalRepository.readAPIKey());
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
                		BioPortalRepositoryItem.bioportalCachedDetails.setProperty(acroynm, format);
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
    	}
    	 
    	return format;
    }

    public IRI getOntologyIRI() {
        return IRI.create(BioPortalRepository.ONTOLOGY_LIST + "/" + acroynm + "/download" + "?apikey=" + BioPortalRepository.readAPIKey());
    }

    public IRI getVersionIRI() {
        return getOntologyIRI();
    }

    public IRI getPhysicalIRI() {
        return getOntologyIRI();
    }
    
    public String toString() {
    	return humanReadableName+" : "+acroynm+" ("+format+")";    	
    }
    
}
