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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;

/**
 * Author: Matthew Horridge, Stuart Owen<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * 
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class OntologyListAccessor {
	
	private final int CONNECT_TIMEOUT = 30000;
	
	private static Logger logger = Logger.getLogger(OntologyListAccessor.class);

    public Collection<BioPortalRepositoryItem> getOntologies() {
        final Collection<BioPortalRepositoryItem> items = new ArrayList<BioPortalRepositoryItem>();
        URL url = null;
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();            
            url = new URL(BioPortalRepository.ONTOLOGY_LIST + "?apikey=" + BioPortalRepository.readAPIKey());
            
            logger.info("Contacting BioPortal REST API at: "+url.toExternalForm());
            
            OntologyListHandler handler = new OntologyListHandler(new BioPortalRepositoryItemHandler() {
                public void handleItem(BioPortalRepositoryItem item) {
                    logger.debug("Found BioportalRepositoryItem handler: " + item);
                    items.add(item);
                }
            });            
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(CONNECT_TIMEOUT);
            if (connection.getResponseCode()==403) {
            	throw new BioPortalAccessDeniedException();
            }            
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            saxParser.parse(bufferedInputStream, handler);
            bufferedInputStream.close();
        }
        catch (ParserConfigurationException e) {
            logger.error("Error parsing configuration",e);
        }
        catch (SAXException e) {
            logger.error("Error handling XML from BioPortal",e);
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
