package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
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
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class OntologyListAccessor {
	
	private static Logger logger = Logger.getLogger(OntologyListAccessor.class);

    public Collection<BioPortalRepositoryItem> getOntologies() {
        final Collection<BioPortalRepositoryItem> items = new ArrayList<BioPortalRepositoryItem>();
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();            
            URL url = new URL(BioPortalRepository.ONTOLOGY_LIST + "?email=" + BioPortalRepository.EMAIL_ID);
            
            logger.info("Contacting BioPortal REST API at: "+url.toExternalForm());
            
            OntologyListHandler handler = new OntologyListHandler(new BioPortalRepositoryItemHandler() {
                public void handleItem(BioPortalRepositoryItem handler) {
                    logger.debug("Found BioportalRepositoryItem handler: "+handler);
                    items.add(handler);
                }
            });
            BufferedInputStream bufferedInputStream = new BufferedInputStream(url.openStream());
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
        catch (IOException e) {
            logger.error("Error communiciating with BioPortal rest API",e);
        }
        return items;
    }
}
