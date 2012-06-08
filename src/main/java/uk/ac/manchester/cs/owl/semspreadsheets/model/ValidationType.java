/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.UILabels;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public enum ValidationType {

    NOVALIDATION(UILabels.getInstance().getFreeTextLabel(), null),
    DIRECTSUBCLASSES(UILabels.getInstance().getDirectSubClassesLabel(), EntityType.CLASS),
    SUBCLASSES(UILabels.getInstance().getSubClassesLabel(), EntityType.CLASS),
    INDIVIDUALS(UILabels.getInstance().getInstancesLabel(), EntityType.NAMED_INDIVIDUAL),
    DIRECTINDIVIDUALS(UILabels.getInstance().getDirectInstancesLabel(), EntityType.NAMED_INDIVIDUAL);

    private String label;

    @SuppressWarnings("rawtypes")
	private EntityType entityType;

    @SuppressWarnings("rawtypes")
	ValidationType(String label, EntityType entityType) {
        this.label = label;
        this.entityType = entityType;
    }

    @SuppressWarnings("rawtypes")
	public EntityType getEntityType() {
        return entityType;
    }    

    public Set<OWLEntity> getEntities(WorkbookManager workbookManager, IRI iri) {
        if (this.equals(SUBCLASSES)) {
            OWLClass cls = workbookManager.getDataFactory().getOWLClass(iri);
            return new HashSet<OWLEntity>(workbookManager.getStructuralReasoner().getSubClasses(cls, false).getFlattened());
        }
        else if (this.equals(DIRECTSUBCLASSES)) {
            OWLClass cls = workbookManager.getDataFactory().getOWLClass(iri);
            return new HashSet<OWLEntity>(workbookManager.getStructuralReasoner().getSubClasses(cls, true).getFlattened());

        }
        else if (this.equals(INDIVIDUALS)) {
            OWLClass cls = workbookManager.getDataFactory().getOWLClass(iri);
            return new HashSet<OWLEntity>(workbookManager.getStructuralReasoner().getInstances(cls, false).getFlattened());

        }
        else if (this.equals(DIRECTINDIVIDUALS)) {
            OWLClass cls = workbookManager.getDataFactory().getOWLClass(iri);
            return new HashSet<OWLEntity>(workbookManager.getStructuralReasoner().getInstances(cls, true).getFlattened());
        }
        else {
            return Collections.emptySet();
        }
    }

    @SuppressWarnings("unchecked")
	public Set<OWLEntity> getEntities(WorkbookManager workbookManager, Collection<Term> terms) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for (Term term : terms) {
            entities.add(workbookManager.getDataFactory().getOWLEntity(getEntityType(), term.getIRI()));
        }
        return entities;
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
