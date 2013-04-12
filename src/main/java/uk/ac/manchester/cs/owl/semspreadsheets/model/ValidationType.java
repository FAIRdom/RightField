/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.skos.SKOSConcept;

import uk.ac.manchester.cs.owl.semspreadsheets.model.skos.SKOSHierarchyReader;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.UILabels;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ValidationEntityType;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public enum ValidationType {	

    FREETEXT(UILabels.getInstance().getFreeTextLabel(), null),
    DIRECTSUBCLASSES(UILabels.getInstance().getDirectSubClassesLabel(), ValidationEntityType.CLASS),
    SUBCLASSES(UILabels.getInstance().getSubClassesLabel(), ValidationEntityType.CLASS),
    INDIVIDUALS(UILabels.getInstance().getInstancesLabel(), ValidationEntityType.NAMED_INDIVIDUAL),
    DIRECTINDIVIDUALS(UILabels.getInstance().getDirectInstancesLabel(), ValidationEntityType.NAMED_INDIVIDUAL),    
    NARROWER(UILabels.getInstance().getSKOSNarrowerLabel(),ValidationEntityType.SKOS_CONCEPT),
    DIRECTNARROWER(UILabels.getInstance().getSKOSDirectNarrowerLabel(),ValidationEntityType.SKOS_CONCEPT);

    private String label;
    private static final Logger logger = Logger.getLogger(ValidationType.class);
    //FIXME: this is also declared in OntoloyTermValidationDescriptor
    private static final IRI NOTHING_IRI = IRI.create("http://www.w3.org/2002/07/owl#Nothing");

    @SuppressWarnings("rawtypes")
	private ValidationEntityType entityType;

    @SuppressWarnings("rawtypes")
	ValidationType(String label, ValidationEntityType entityType) {
        this.label = label;
        this.entityType = entityType;
    }

    @SuppressWarnings("rawtypes")
	public ValidationEntityType getEntityType() {
        return entityType;
    }    
    
    public List<Term> getTerms(OntologyManager ontologyManager, IRI iri) {
    	List<Term> terms;
    	if (this.equals(SUBCLASSES) || this.equals(DIRECTSUBCLASSES) || this.equals(DIRECTINDIVIDUALS) || this.equals(INDIVIDUALS)) {
    		terms = new ArrayList<Term>();
    		for (OWLEntity entity : getOWLEntities(ontologyManager, iri)) {
    			if (!entity.getIRI().equals(NOTHING_IRI)) {
            		logger.debug("Adding term "+entity.getIRI()+" to list of terms");        	
                    terms.add(new Term(entity.getIRI(), ontologyManager.getRendering(entity)));
            	}        	
            	else {
            		logger.debug("Ignoring the term "+entity.getIRI().toString());
            	}
    		}
    	}
    	else if (this.equals(NARROWER) || this.equals(DIRECTNARROWER)){
    		terms = getSKOSTerms(ontologyManager, iri);    		    		
    	}
    	else {
    		terms = Collections.emptyList();
    	}
    	Collections.sort(terms);
    	return terms;
    }
    
    private List<Term> getSKOSTerms(OntologyManager ontologyManager, IRI iri) {
    	List<Term> terms = new ArrayList<Term>();
    	Set<OWLOntology> ontologies = ontologyManager.getOntologiesForEntityIRI(iri);
    	for (OWLOntology ontology : ontologies) {
    		SKOSHierarchyReader reader = new SKOSHierarchyReader(ontologyManager, ontology);
    		SKOSConcept concept = reader.getSKOSConcept(iri.toURI());
    		
    		String name = concept.getURI().getFragment();
    		
    		
    		
    		Set<SKOSConcept> narrower = reader.getNarrowerThan(concept);
    		for (SKOSConcept narrow : narrower) {
    			name = narrow.getURI().getFragment();
    			terms.add(new Term(IRI.create(narrow.getURI()),name));
    		}    		    	
    	}  
    	return terms;
    }

    private Set<OWLEntity> getOWLEntities(OntologyManager ontologyManager, IRI iri) {
        if (this.equals(SUBCLASSES)) {
            OWLClass cls = ontologyManager.getDataFactory().getOWLClass(iri);
            return new HashSet<OWLEntity>(ontologyManager.getStructuralReasoner().getSubClasses(cls, false).getFlattened());
        }
        else if (this.equals(DIRECTSUBCLASSES)) {
            OWLClass cls = ontologyManager.getDataFactory().getOWLClass(iri);
            return new HashSet<OWLEntity>(ontologyManager.getStructuralReasoner().getSubClasses(cls, true).getFlattened());
        }
        else if (this.equals(INDIVIDUALS)) {
            OWLClass cls = ontologyManager.getDataFactory().getOWLClass(iri);
            return new HashSet<OWLEntity>(ontologyManager.getStructuralReasoner().getInstances(cls, false).getFlattened());
        }
        else if (this.equals(DIRECTINDIVIDUALS)) {
            OWLClass cls = ontologyManager.getDataFactory().getOWLClass(iri);
            return new HashSet<OWLEntity>(ontologyManager.getStructuralReasoner().getInstances(cls, true).getFlattened());
        }        
        else {
            return Collections.emptySet();
        }
    }
    

    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum type should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return label;
    }


}
