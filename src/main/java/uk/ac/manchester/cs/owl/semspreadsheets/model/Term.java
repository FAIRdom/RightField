/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.IRI;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
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
    
    /**
     * @return an improved formatting of the name - e.g removing underscores between words
     */
    public String getFormattedName() {
    	return name.replaceAll("_", " ");
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
