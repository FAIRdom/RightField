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
