/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;

public class OntologyListHandlerTest {
	
	@Test
	public void testListHandling() throws Exception {
        final Collection<RepositoryItem> collection = new ArrayList<RepositoryItem>();
		BioPortalOntologyListHandler handler = new BioPortalOntologyListHandler(new BioPortalRepositoryItemHandler() {
            public void handleItem(RepositoryItem handler) {        
            	collection.add(handler);
            }
        });
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();		
        SAXParser saxParser = saxParserFactory.newSAXParser();        
        BufferedInputStream bufferedInputStream = new BufferedInputStream(getDummyXMLStream("bioportal_responses/dummy_ontology_list.xml"));
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
	
	@Test
	public void testFormatFiltering() throws Exception {
		final Collection<RepositoryItem> collection = new ArrayList<RepositoryItem>();
		BioPortalOntologyListHandler handler = new BioPortalOntologyListHandler(new BioPortalRepositoryItemHandler() {
            public void handleItem(RepositoryItem handler) {        
            	collection.add(handler);
            }
        });
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(getDummyXMLStream("bioportal_responses/dummy_ontology_list_bad_formats.xml"));
        saxParser.parse(bufferedInputStream, handler);
        bufferedInputStream.close();
        
        assertEquals(1,collection.size());
        RepositoryItem item = collection.iterator().next();
        assertEquals("OWL-DL",item.getFormat());
	}
	
	@Test
	public void testSkipsNonNumericIDsFiltering() throws Exception {
		final Collection<RepositoryItem> collection = new ArrayList<RepositoryItem>();
		BioPortalOntologyListHandler handler = new BioPortalOntologyListHandler(new BioPortalRepositoryItemHandler() {
            public void handleItem(RepositoryItem handler) {        
            	collection.add(handler);
            }
        });
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(getDummyXMLStream("bioportal_responses/ontology_list_with_non_numeric_ids.xml"));
        saxParser.parse(bufferedInputStream, handler);
        bufferedInputStream.close();
        
        int [] ontologyIds = {10,20};
        String [] labels = {"A-label","B-label"};
        String [] formats = {"OWL-DL","OBO"};
        BioPortalRepositoryItem [] items = collection.toArray(new BioPortalRepositoryItem[0]);
        assertEquals(2,items.length);
        for (int i=0;i<items.length;i++) {
        	BioPortalRepositoryItem item = items[i];
        	String asString = labels[i]+" : "+ontologyIds[i]+" ("+formats[i]+")";
        	assertEquals(asString,item.toString());
        }
	}		
	
	private InputStream getDummyXMLStream(String filename) throws Exception {
		return OntologyListHandlerTest.class.getResourceAsStream("/"+filename);
	}

}
