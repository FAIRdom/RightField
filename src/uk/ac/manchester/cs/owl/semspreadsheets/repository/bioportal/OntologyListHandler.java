package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class OntologyListHandler extends DefaultHandler {

    public static final String ONTOLOGY_ID_ELEMENT_NAME = "ontologyId";

    public static final String DISPLAY_LABEL_ELEMENT_NAME = "displayLabel";

    private NullElementState nullElementState = new NullElementState();

    private ElementState elementState = nullElementState;

    private OntologyIDState ontologyIDState = new OntologyIDState();

    private DisplayLabelState displayLabelState = new DisplayLabelState();

    private int lastOntologyID;

    private String lastDisplayLabel;

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
        else {
            elementState = nullElementState;
        }
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

    private class DisplayLabelState implements ElementState {

        public void characters(char[] ch, int start, int length) throws SAXException {
            lastDisplayLabel = new String(ch, start, length);
            itemHandler.handleItem(new BioPortalRepositoryItem(lastOntologyID, lastDisplayLabel));
        }
    }
}
