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
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 20-Sep-2009
 */
public class OntologyTermValidationDescriptor implements Serializable {

	private static final long serialVersionUID = 3278347556332276152L;

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
        Collections.sort(this.terms);
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
