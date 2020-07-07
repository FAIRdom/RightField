/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepository;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class OntologyTermValidationDescriptor implements Serializable {	
	
	private static final long serialVersionUID = 3278347556332276152L;

	private ValidationType type;

    private IRI entityIRI;

    private Map<IRI, IRI> ontologyIRI2PhysicalIRIMap = new HashMap<IRI, IRI>();

    private List<Term> terms = new ArrayList<Term>();
    
    private OWLPropertyItem propertyItem;
    
    private static final IRI NOTHING_IRI = IRI.create("http://www.w3.org/2002/07/owl#Nothing");
    
    /**
     * A FREETEXT type, but with a property
     * @param ontologyManager
     * @param propertyItem
     */
    public OntologyTermValidationDescriptor(OWLPropertyItem propertyItem,OntologyManager ontologyManager) {
    	this(ValidationType.FREETEXT,NOTHING_IRI,propertyItem,
                ValidationType.FREETEXT.getTerms(ontologyManager, NOTHING_IRI),ontologyManager);
    }

    public OntologyTermValidationDescriptor(ValidationType type, IRI entityIRI, OWLPropertyItem propertyItem, Map<IRI, IRI> ontologyIRI2PhysicalIRIMap, Map<IRI, String> terms) {
        this.type = type;
        this.entityIRI = entityIRI;
		this.propertyItem = propertyItem;		
        this.ontologyIRI2PhysicalIRIMap = new HashMap<IRI, IRI>(ontologyIRI2PhysicalIRIMap);        
        for(IRI iri : terms.keySet()) {
            this.terms.add(new Term(iri, terms.get(iri)));
        }
        Collections.sort(this.terms);
    }
        
    public OntologyTermValidationDescriptor(ValidationType type, IRI entityIRI, OWLPropertyItem propertyItem, List<Term> terms, OntologyManager ontologyManager) {
        this.type = type;
        this.entityIRI = entityIRI;
		this.propertyItem = propertyItem;
		this.terms = terms != null ? terms : type.getTerms(ontologyManager, entityIRI);
        ontologyIRI2PhysicalIRIMap = new HashMap<IRI, IRI>();
		resolveOntologyIRIMap(entityIRI, propertyItem, ontologyManager);
    }

    private void resolveOntologyIRIMap(IRI entityIRI,
			OWLPropertyItem propertyItem, OntologyManager ontologyManager) {
		for (OWLOntology ontology : ontologyManager.getOntologiesForEntityIRI(entityIRI,propertyItem)) {						
			
				IRI documentIRI = ontologyManager.getOWLOntologyManager()
						.getOntologyDocumentIRI(ontology);
				documentIRI = BioPortalRepository
						.removeBioPortalAPIKey(documentIRI);
				ontologyIRI2PhysicalIRIMap.put(ontology.getOntologyID()
						.getOntologyIRI(), documentIRI);			
		}		
	}    	
    
    /**
     * @return whether this validation defines a literal, i.e has a property but is FREETEXT
     */
    public boolean definesLiteral() {
    	return (getOWLPropertyItem()!=null && getEntityIRI().equals(NOTHING_IRI) && getType().equals(ValidationType.FREETEXT));
    }        

    public OWLPropertyItem getOWLPropertyItem() {
    	return propertyItem;
    }
    
    public Set<IRI> getOntologyIRIs() {
        return ontologyIRI2PhysicalIRIMap.keySet();
    }

    public IRI getPhysicalIRIForOntologyIRI(IRI ontologyIRI) {
        return ontologyIRI2PhysicalIRIMap.get(ontologyIRI);
    }

    public ValidationType getType() {
        return type;
    }    

    public IRI getEntityIRI() {
        return entityIRI;
    }

    public int hashCode() {
    	int entityHash=entityIRI==null ? "null".hashCode() : entityIRI.hashCode();
    	int propertyHash=propertyItem==null ? "null".hashCode() : propertyItem.hashCode();
    	
    	return propertyHash+ type.hashCode() + entityHash + ontologyIRI2PhysicalIRIMap.hashCode();    	        
    }

    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof OntologyTermValidationDescriptor)) {
            return false;
        }
        OntologyTermValidationDescriptor other = (OntologyTermValidationDescriptor) obj;
        return other.getEntityIRI().equals(this.getEntityIRI()) &&
                other.getOntologyIRIs().equals(this.getOntologyIRIs()) &&
                other.getType().equals(this.getType());
    }

    public Collection<Term> getTerms() {
        return new ArrayList<Term>(terms);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OntologyTermValidationDescriptor(");
        sb.append(type);
        sb.append(" ");
        sb.append(entityIRI.toQuotedString());
        for(IRI ontologyIRI : ontologyIRI2PhysicalIRIMap.keySet()) {
            sb.append(" Ontology(");
            sb.append(ontologyIRI.toQuotedString());
            sb.append(")");
        }
        sb.append("Terms(");
        for(Term term : terms) {
            sb.append(" Term(");
            sb.append(term.toString());
            sb.append(") ");
        }
        sb.append(" )");
        sb.append(")");
        return sb.toString();
    }
}
