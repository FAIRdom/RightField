package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.*;
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
public class OntologyTermValidationDescriptor {

    private ValidationType type;

    private IRI entityIRI;

    private Map<IRI, IRI> ontologyIRI2PhysicalIRIMap = new HashMap<IRI, IRI>();

    private List<Term> terms;

    public OntologyTermValidationDescriptor(ValidationType type, IRI entityIRI, Map<IRI, IRI> ontologyIRI2PhysicalIRIMap, Map<IRI, String> terms) {
        this.type = type;
        this.entityIRI = entityIRI;
        this.ontologyIRI2PhysicalIRIMap = new HashMap<IRI, IRI>(ontologyIRI2PhysicalIRIMap);
        this.terms = new ArrayList<Term>();
        for(IRI iri : terms.keySet()) {
            this.terms.add(new Term(iri, terms.get(iri)));
        }
    }

    public OntologyTermValidationDescriptor(ValidationType type, IRI entityIRI, WorkbookManager workbookManager) {
        this.type = type;
        this.entityIRI = entityIRI;
        ontologyIRI2PhysicalIRIMap = new HashMap<IRI, IRI>();
        for(OWLOntology ont : workbookManager.getLoadedOntologies()) {
            IRI documentIRI = workbookManager.getOntologyManager().getOntologyDocumentIRI(ont);
            ontologyIRI2PhysicalIRIMap.put(ont.getOntologyID().getOntologyIRI(), documentIRI);
        }
        Set<OWLEntity> entities = type.getEntities(workbookManager, entityIRI);
        terms = new ArrayList<Term>();
        for(OWLEntity term : entities) {
            terms.add(new Term(term.getIRI(), workbookManager.getRendering(term)));
        }
        Collections.sort(terms);
    }

    public Set<IRI> getOntologyIRIs() {
        return ontologyIRI2PhysicalIRIMap.keySet();
    }

    public IRI getPhysicslIRIForOntologyIRI(IRI ontologyIRI) {
        return ontologyIRI2PhysicalIRIMap.get(ontologyIRI);
    }

    public ValidationType getType() {
        return type;
    }

    public Set<OWLEntity> getEntities(WorkbookManager workbookManager) {
        return type.getEntities(workbookManager, terms);
    }

    public IRI getEntityIRI() {
        return entityIRI;
    }

    public int hashCode() {
        return type.hashCode() + entityIRI.hashCode() + ontologyIRI2PhysicalIRIMap.hashCode();
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
