package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OntologyListHandlerTest {
	
	@Test
	public void testListHandling() throws Exception {
        final Collection<BioPortalRepositoryItem> collection = new ArrayList<BioPortalRepositoryItem>();
		OntologyListHandler handler = new OntologyListHandler(new BioPortalRepositoryItemHandler() {
            public void handleItem(BioPortalRepositoryItem handler) {        
            	collection.add(handler);
            }
        });
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(getDummyXMLStream());
        saxParser.parse(bufferedInputStream, handler);
        bufferedInputStream.close();
        
        int [] ontologyIds = {10,20,30};
        String [] labels = {"A-label","B-label","C-label"};
        String [] formats = {"OWL-DL","OBO","OWL"};
        BioPortalRepositoryItem [] items = collection.toArray(new BioPortalRepositoryItem[0]);
        assertEquals(3,items.length);
        for (int i=0;i<items.length;i++) {
        	BioPortalRepositoryItem item = items[i];
        	String asString = labels[i]+" : "+ontologyIds[i]+" ("+formats[i]+")";
        	assertEquals(asString,item.toString());
        }
	}
	
	private InputStream getDummyXMLStream() throws Exception {
		return OntologyListHandlerTest.class.getResourceAsStream("/dummy_ontology_list.xml");
	}

}
