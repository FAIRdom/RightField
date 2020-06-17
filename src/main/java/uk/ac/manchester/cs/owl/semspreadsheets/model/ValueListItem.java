package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.IRI;

//AW added this as regular class, not inner class
 public class ValueListItem implements Comparable<ValueListItem> {

        private Term term;
                
        private ValidationType type;

        public ValueListItem(Term term, ValidationType type) {
            this.term = term;
            this.type = type;
        }

        @Override
        public String toString() {
            return getName();
        }
        
        public String getName() {
        	return term.getFormattedName();
        }
        
        public IRI getEntityIRI() {
        	return term.getIRI();
        }
        
        public ValidationType getType() {
            return type;
        }
        
        public Term getTerm() {
        	return term;
        }

        public int compareTo(ValueListItem o) {
            return getTerm().compareTo(o.getTerm());
        }
    }