package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

import java.util.List;
import java.util.ArrayList;
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
