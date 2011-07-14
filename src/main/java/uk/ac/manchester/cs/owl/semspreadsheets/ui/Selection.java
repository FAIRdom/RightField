package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 20-Sep-2009
 */
public class Selection {

    private Range range;

    public Selection(Range range) {
        this.range = range;
    }

    public Selection() {
        range = null;
    }

    public boolean isEmpty() {
        return range == null;
    }

    public boolean isCellSelection() {
        return range != null;
    }
}
