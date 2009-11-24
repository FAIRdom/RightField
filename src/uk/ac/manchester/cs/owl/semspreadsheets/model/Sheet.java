package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.Collection;
import java.io.File;
import java.io.IOException;
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
public interface Sheet {

    String getName();

    void setName(String name);

    Workbook getWorkbook();

    boolean isHidden();

    void setVeryHidden(boolean b);

    void setHidden(boolean b);

    int getMaxRows();

    int getMaxColumns();

    int getColumnWidth(int col);

    void clearAllCells();

    boolean isCellAt(int col, int row);

    Cell getCellAt(int col, int row);

    Cell addCellAt(int col, int row);

    void clearCellAt(int col, int row);

    void addValidation(String namedRange, int firstCol, int firstRow, int lastCol, int lastRow);

    void removeValidation(Validation validation);

    Collection<Validation> getValidations();

    Collection<Validation> getIntersectingValidations(Range range);

    Collection<Validation> getContainingValidations(Range range);

    Validation getValidationAt(int col, int row);

    void clearValidation();




}
