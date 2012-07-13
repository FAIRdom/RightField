/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Methods related to fetching OWL Data or Object properties, also filtering by ontology and validation type.
 * Although possible to use alone, this class is delegated to via the {@link OntologyManager}
 * 
 * @author Stuart Owen
 *
 */

public class OWLPropertyHandler {
	
	private final OWLOntologyManager owlOntologyManager;

	public OWLPropertyHandler(OWLOntologyManager owlOntologyManager) {
		this.owlOntologyManager = owlOntologyManager;		
	}
	
	/**
	 * for Sublasses the Object and Data properties are returned. For Inviduals and Free text, only Data types are returned
	 * @param ontology
	 * @param type
	 * @return set of the appropriate properties.
	 */
	public Set<OWLPropertyItem> getAllOWLProperties(OWLOntology ontology, ValidationType type) {
		Set<OWLPropertyItem> properties = getOWLDataProperties(ontology);		
		if (type!=ValidationType.FREETEXT) {
			properties.addAll(getOWLObjectProperties(ontology));
		}
		return properties;
	}
	
	/**
     * 
     * @param ontology
     * @return all the Object and Data property items for the given ontology
     */
    public Set<OWLPropertyItem> getAllOWLProperties(OWLOntology ontology) {
    	Set<OWLPropertyItem> properties = getOWLDataProperties(ontology);
    	properties.addAll(getOWLObjectProperties(ontology));
    	return properties;
    }
    
    /**
     * @param ontology
     * @return Data property items defined in the given ontology
     */
    public Set<OWLPropertyItem> getOWLDataProperties(OWLOntology ontology) {
    	Set<OWLPropertyItem> properties = new HashSet<OWLPropertyItem>();    	
		for (OWLDataProperty property : ontology.getDataPropertiesInSignature())
		{
			if (!property.isTopEntity()) {
				properties.add(new OWLPropertyItem(property));
			}
		}    		    	    	
    	return properties;
    }
    
    /**
     * @param ontology
     * @return Object property items defined in the given ontology
     */
    public Set<OWLPropertyItem> getOWLObjectProperties(OWLOntology ontology) {
    	Set<OWLPropertyItem> properties = new HashSet<OWLPropertyItem>();    	
		for (OWLObjectProperty property : ontology.getObjectPropertiesInSignature())
		{
			if (!property.isTopEntity()) {
				properties.add(new OWLPropertyItem(property));
			}
		}    		    	    
    	return properties;
    }
    
    /**
     * @return all Object and Data properties defined within all loaded ontologies
     */
    public Set<OWLPropertyItem> getAllOWLProperties() {
    	Set<OWLPropertyItem> properties = getOWLDataProperties();
    	properties.addAll(getOWLObjectProperties());
    	return properties;
    }
    
    /**     
     * @return Data properties defined within all loaded ontologies
     */
    public Set<OWLPropertyItem> getOWLDataProperties() {
    	Set<OWLPropertyItem> properties = new HashSet<OWLPropertyItem>();
    	for (OWLOntology ontology : getAllOntologies()) {
    		properties.addAll(getOWLDataProperties(ontology)); 		
    	}    	
    	return properties;
    }
    
    /**
     * @return Object properties defined within all loaded ontologies
     */
    public Set<OWLPropertyItem> getOWLObjectProperties() {
    	Set<OWLPropertyItem> properties = new HashSet<OWLPropertyItem>();
    	for (OWLOntology ontology : getAllOntologies()) {
    		properties.addAll(getOWLObjectProperties(ontology));    		
    	}    	
    	return properties;
    }
    
    private Set<OWLOntology> getAllOntologies() {
    	return owlOntologyManager.getOntologies();
    }

}
