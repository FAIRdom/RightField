package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Validation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;

import java.util.List;
import java.util.Collections;
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
 * Date: 18-Sep-2009
 */
public class ValidationImpl implements Validation {

    private String list;

    private Sheet sheet;

    private int fromColumn;

    private int toColumn;

    private int fromRow;

    private int toRow;

    public ValidationImpl(String list, Sheet sheet, int fromColumn, int toColumn, int fromRow, int toRow) {
        this.list = list;
        this.sheet = sheet;
        this.fromColumn = fromColumn;
        this.toColumn = toColumn;
        this.fromRow = fromRow;
        this.toRow = toRow;
    }

    public Range getRange() {
        return new Range(sheet, fromColumn, fromRow, toColumn, toRow);
    }

    public Sheet getSheet() {
        return sheet;
    }

    public String getListName() {
        return list;
    }

    public int getFirstColumn() {
        return fromColumn;
    }

    public int getLastColumn() {
        return toColumn;
    }

    public int getFirstRow() {
        return fromRow;
    }

    public int getLastRow() {
        return toRow;
    }





    public boolean contains(int col, int row) {
        return fromColumn >= col && row <= toColumn && row >= fromRow && row <= toRow;
    }
}
