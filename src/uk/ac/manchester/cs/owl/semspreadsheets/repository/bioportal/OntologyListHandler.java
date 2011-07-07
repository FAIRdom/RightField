package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Author: Matthew Horridge, Stuart Owen<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 * 
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class OntologyListHandler extends DefaultHandler {

    public static final String ONTOLOGY_ID_ELEMENT_NAME = "ontologyId";

    public static final String DISPLAY_LABEL_ELEMENT_NAME = "displayLabel";
    
    public static final String FORMAT_ELEMENT_NAME = "format";
    
    public static final String BEAN_ELEMENT_NAME = "ontologyBean";
    
    private static final List<String> FORMAT_WHITE_LIST = Arrays.asList(new String []{"OWL","OBO","OWL-FULL","OWL-DL"});

    private NullElementState nullElementState = new NullElementState();

    private ElementState elementState = nullElementState;

    private OntologyIDState ontologyIDState = new OntologyIDState();

    private DisplayLabelState displayLabelState = new DisplayLabelState();
    
    private FormatState formatState = new FormatState();

    private int lastOntologyID;

    private String lastDisplayLabel;

    private String lastFormat;
    
    private BioPortalRepositoryItemHandler itemHandler;

    public OntologyListHandler(BioPortalRepositoryItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(qName.equals(ONTOLOGY_ID_ELEMENT_NAME)) {
            elementState = ontologyIDState;
        }
        else if(qName.equals(DISPLAY_LABEL_ELEMENT_NAME)) {
            elementState = displayLabelState;
        }
        else if (qName.equals(FORMAT_ELEMENT_NAME)) {
        	elementState = formatState;
        }
        else {
            elementState = nullElementState;
        }
    }
    
    


    @Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals(BEAN_ELEMENT_NAME)) {
			if (acceptableFormat(lastFormat)) {
				itemHandler.handleItem(new BioPortalRepositoryItem(lastOntologyID, lastDisplayLabel,lastFormat));
			}			
			lastOntologyID=0;
			lastDisplayLabel=null;
			lastFormat=null;
		}
	}

	private boolean acceptableFormat(String format) {
		return FORMAT_WHITE_LIST.contains(format.toUpperCase());
	}

	@Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        elementState.characters(ch, start, length);
    }



    private interface ElementState {

        void characters(char [] ch, int start, int length) throws SAXException;
    }

    private class NullElementState implements ElementState {

        public void characters(char[] ch, int start, int length) throws SAXException {
        }
    }

    private class OntologyIDState implements ElementState {

        public void characters(char[] ch, int start, int length) throws SAXException {
            lastOntologyID = Integer.parseInt(new String(ch, start,  length).trim());
        }
    }
    
    private class FormatState implements ElementState {
    	public void characters(char[] ch, int start, int length) throws SAXException {
            lastFormat = new String(ch, start, length);
        }
    }

    private class DisplayLabelState implements ElementState {

        public void characters(char[] ch, int start, int length) throws SAXException {
            lastDisplayLabel = new String(ch, start, length);            
        }
    }
}
