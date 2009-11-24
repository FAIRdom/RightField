package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.IRI;
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
