package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.IRI;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public class Term implements Comparable<Term> {

    private IRI iri;

    private String name;

    public Term(IRI iri, String name) {
        this.iri = iri;
        this.name = name;
    }

    public IRI getIRI() {
        return iri;
    }

    public String getName() {
        return name;
    }

    public int compareTo(Term o) {
        int diff = name.compareToIgnoreCase(o.name);
        if(diff != 0) {
            return diff;
        }
        else {
            return iri.compareTo(o.iri);
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode() + iri.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof Term)) {
            return false;
        }
        Term other = (Term) obj;
        return other.name.equals(this.name) && other.iri.equals(this.iri);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Term(");
        sb.append(name);
        sb.append(" ");
        sb.append(iri.toQuotedString());
        sb.append(")");
        return sb.toString();
    }
}
