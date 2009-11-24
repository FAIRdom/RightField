package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.inference.OWLReasonerAdapter;
import org.semanticweb.owlapi.inference.OWLReasonerException;

import java.util.*;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
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
 * Date: 20-Sep-2009
 */
public enum ValidationType {

    NOVALIDATION("Any", null),
    DIRECTSUBCLASSES("Subclass names", EntityType.CLASS),
    SUBCLASSES("Descendent class names", EntityType.CLASS),
    INDIVIDUALS("Individual names", EntityType.NAMED_INDIVIDUAL),
    DIRECTINDIVIDUALS("Direct individual names", EntityType.NAMED_INDIVIDUAL);

    private String label;

    private EntityType entityType;

    ValidationType(String label, EntityType entityType) {
        this.label = label;
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Set<OWLEntity> getEntities(WorkbookManager workbookManager, IRI iri) {
        try {
            if(this.equals(SUBCLASSES)) {
                OWLClass cls = workbookManager.getDataFactory().getOWLClass(iri);
                return new HashSet<OWLEntity>(OWLReasonerAdapter.flattenSetOfSets(workbookManager.getReasoner().getDescendantClasses(cls)));
            }
            else if(this.equals(DIRECTSUBCLASSES)) {
                OWLClass cls = workbookManager.getDataFactory().getOWLClass(iri);
                return new HashSet<OWLEntity>(OWLReasonerAdapter.flattenSetOfSets(workbookManager.getReasoner().getSubClasses(cls)));

            }
            else if(this.equals(INDIVIDUALS)) {
                OWLClass cls = workbookManager.getDataFactory().getOWLClass(iri);
                return new HashSet<OWLEntity>(workbookManager.getReasoner().getIndividuals(cls, false));

            }
            else if(this.equals(DIRECTINDIVIDUALS)) {
                OWLClass cls = workbookManager.getDataFactory().getOWLClass(iri);
                return new HashSet<OWLEntity>(workbookManager.getReasoner().getIndividuals(cls, true));

            }
            else {
                return Collections.emptySet();
            }
        }
        catch (OWLReasonerException e) {

            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    public Set<OWLEntity> getEntities(WorkbookManager workbookManager, Collection<Term> terms) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for(Term term : terms) {
            entities.add(workbookManager.getDataFactory().getOWLEntity(getEntityType(), term.getIRI()));
        }
        return entities;
    }

    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum type should override this
     * method when a more "programmer-friendly" string form exists.
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return label;
    }


}
