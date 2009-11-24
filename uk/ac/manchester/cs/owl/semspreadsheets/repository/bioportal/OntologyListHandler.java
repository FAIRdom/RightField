package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Collection;
/*
 * Copyright (C) 2009, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
