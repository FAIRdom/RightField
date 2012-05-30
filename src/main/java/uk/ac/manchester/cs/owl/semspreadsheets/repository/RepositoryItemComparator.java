/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.repository;

import java.util.Comparator;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class RepositoryItemComparator implements Comparator<RepositoryItem> {

    public int compare(RepositoryItem o1, RepositoryItem o2) {
        return o1.getHumanReadableName().compareToIgnoreCase(o2.getHumanReadableName());
    }
}
