package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 20-Sep-2009
 */
public class CellSelectionModel {

    private List<CellSelectionListener> listeners = new ArrayList<CellSelectionListener>();

    private WorkbookManager workbookManager;

    private Range range;

    public CellSelectionModel(WorkbookManager workbookManager) {
        this.workbookManager = workbookManager;
    }

    public void addCellSelectionListener(CellSelectionListener listener) {
        listeners.add(listener);
    }

    public void removeCellSelectionListener(CellSelectionListener listener) {
        listeners.remove(listener);
    }

    public void setSelectedRange(Range range) {
        this.range = range;
        for(CellSelectionListener listener : listeners) {
            listener.selectionChanged(range);
        }
    }

    public Range getSelectedRange() {
        return range;
    }
}
