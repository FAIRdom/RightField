package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
 * Date: 07-Nov-2009
 */
public class FormatBoldAction extends SelectedCellsAction {

    public FormatBoldAction(WorkbookFrame workbookFrame) {
        super("Cells bold", workbookFrame);
        setAcceleratorKey(KeyEvent.VK_B);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        Range range = getSelectedRange();
        if (range.isCellSelection()) {
            for(int col = range.getFromColumn(); col < range.getToColumn() + 1; col++) {
                for(int row = range.getFromRow(); row < range.getToRow() + 1; row++) {
                    Cell cell = range.getSheet().getCellAt(col, row);
                    if (cell != null) {
                        cell.setBold(true);
                    }
                }
            }
        }
    }
}
