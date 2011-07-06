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
        final Collection<BioPortalRepositoryItem> items = new ArrayList<BioPortalRepositoryItem>();
		OntologyListHandler handler = new OntologyListHandler(new BioPortalRepositoryItemHandler() {
            public void handleItem(BioPortalRepositoryItem handler) {        
                items.add(handler);
            }
        });
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(getDummyXMLStream());
        saxParser.parse(bufferedInputStream, handler);
        bufferedInputStream.close();
        assertEquals(3,items.size());
        fail("Test still incomplete");
	}
	
	private InputStream getDummyXMLStream() throws Exception {
		return OntologyListHandlerTest.class.getResourceAsStream("/dummy_ontology_list.xml");
	}

}
