/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public class OntologyTermValidation {

    private OntologyTermValidationDescriptor validationDescriptor;

    private Range range;

    public OntologyTermValidation(OntologyTermValidationDescriptor validationDescriptor, Range range) {
        this.validationDescriptor = validationDescriptor;
        this.range = range;
    }

    public OntologyTermValidationDescriptor getValidationDescriptor() {
        return validationDescriptor;
    }

    public void setRange(Range newRange) {
    	range=newRange;
    }
    
    public Range getRange() {
        return range;
    }

    public int hashCode() {
        return range.hashCode() + validationDescriptor.hashCode();
    }

    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof OntologyTermValidation)) {
            return false;
        }
        OntologyTermValidation other = (OntologyTermValidation) obj;
        return other.getValidationDescriptor().equals(this.getValidationDescriptor()) &&
                other.getRange().equals(this.getRange());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OntologyTermValidationRange(Range(");
        sb.append(range);
        sb.append(")" );
        sb.append(validationDescriptor);
        sb.append(")");
        return sb.toString();
    }
}
