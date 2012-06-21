/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

/**
 * Encodes and decodes the string that defines a property as a formula to allow it to be emedded as a custom validation in a cell (when used with free text).
 * 
 * e.g Property ObjectType, with IRI http://www.mygrid.org.uk/JERMOntology#hasType, with associated hidden sheet defining the ontology details becomes:
 * property^hidden_sheet^<http://www.mygrid.org.uk/JERMOntology#hasType>^OBJECT_TYPE
 * 
 * @author Stuart Owen
 *
 */
public class PropertyValidationForumlaDefinition {
	
	private static final Logger logger = Logger.getLogger(PropertyValidationForumlaDefinition.class);
	
	/**
	 * Defines the seperator used in the encoding, currently ^ is chosen since it is not a valid IRI character, so makes splitting the string easier.
	 */
	public static final String SEPERATOR = "^";
	
	public static String encode(String hiddenSheetName,OWLPropertyItem propertyItem) {
		return "property^"+hiddenSheetName+"^"+propertyItem.getIRI().toQuotedString()+"^"+propertyItem.getPropertyType().name();
	}

	public static OWLPropertyItem decode(String formula) {
		OWLPropertyItem item = null;
		String [] bits = formula.split("\\"+PropertyValidationForumlaDefinition.SEPERATOR);
		if (bits.length==4) {
			String iri = bits[2];
			String type = bits[3];
			iri = iri.substring(1, iri.length()-1);
			
			item = new OWLPropertyItem(IRI.create(iri), OWLPropertyType.valueOf(type));			
		}
		else {
			//FIXME: should raise exception rather than just return null
			logger.warn("Unexpected number of elements in encoded property "+formula);
		}    

		return item;
	}
	
	public static OntologyTermValidation constructFromValidation(Validation validation,OntologyManager ontologyManager) {
		OWLPropertyItem item = PropertyValidationForumlaDefinition.decode(validation.getFormula());
		OntologyTermValidation termValidation = null;
		//FIXME: should raise exception rather than just return null
		if (item!=null) {
			OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(item, ontologyManager);
			termValidation = new OntologyTermValidation(descriptor, validation.getRange());
		}
		return termValidation;
	}
}
