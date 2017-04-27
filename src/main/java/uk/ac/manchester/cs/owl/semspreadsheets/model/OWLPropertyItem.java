/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

/**
 * Simple wrapper for {@link OWLDataProperty} or {@link OWLObjectProperty}
 * since neither have a common superclass.
 * 
 * @author Stuart Owen
 *
 */
public class OWLPropertyItem {	

	private OWLDataProperty dataProperty;
	private OWLObjectProperty objectProperty;
	
	public OWLPropertyItem(IRI iri,OWLPropertyType type) {
		if (type==OWLPropertyType.DATA_PROPERTY) {
			dataProperty=new OWLDataPropertyImpl(iri);
		}
		else {
			objectProperty=new OWLObjectPropertyImpl(iri);
		}
	}
	
	public OWLPropertyItem(OWLObjectProperty objectProperty) {		
		this.objectProperty = objectProperty;
	}

	public OWLPropertyItem(OWLDataProperty dataProperty) {
		this.dataProperty = dataProperty;		
	}
	
	public Object getProperty() {
		if (dataProperty!=null) {
			return dataProperty;			
		}
		else {
			return objectProperty;
		}
	}
	
	public OWLPropertyType getPropertyType() {
		if (dataProperty!=null) {
			return OWLPropertyType.DATA_PROPERTY;			
		}
		else {
			return OWLPropertyType.OBJECT_PROPERTY;
		}
	}
	
	public IRI getIRI() {
		if (dataProperty!=null) {
			return dataProperty.getIRI();			
		}
		else {
			return objectProperty.getIRI();
		}
	}

	@Override
	public String toString() {
		return getIRI().getFragment().toString();
	}
	
	@Override
	public int hashCode() {
		return getIRI().hashCode()+getPropertyType().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OWLPropertyItem) {
			OWLPropertyItem item = (OWLPropertyItem)obj;
			return item.getIRI().equals(getIRI()) && item.getPropertyType().equals(getPropertyType());
		}
		else {
			return false;
		}
	}
	
	

}
