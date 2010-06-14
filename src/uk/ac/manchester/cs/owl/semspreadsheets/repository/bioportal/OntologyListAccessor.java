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

import org.xml.sax.SAXException;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class OntologyListAccessor {

    public Collection<BioPortalRepositoryItem> getOntologies() {
        final Collection<BioPortalRepositoryItem> items = new ArrayList<BioPortalRepositoryItem>();
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            URL url = new URL(BioPortalRepository.ONTOLOGY_LIST + "?" + BioPortalRepository.EMAIL_ID);
            OntologyListHandler handler = new OntologyListHandler(new BioPortalRepositoryItemHandler() {
                public void handleItem(BioPortalRepositoryItem handler) {
                    System.out.println(handler);

                    items.add(handler);
                }
            });
            BufferedInputStream bufferedInputStream = new BufferedInputStream(url.openStream());
            saxParser.parse(bufferedInputStream, handler);
            bufferedInputStream.close();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (SAXException e) {
            e.printStackTrace();
        }
        catch (UnknownHostException e) {
            ErrorHandler.getErrorHandler().handleError(e);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }
}
