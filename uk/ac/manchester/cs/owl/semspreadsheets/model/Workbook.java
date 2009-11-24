package uk.ac.manchester.cs.owl.semspreadsheets.model;

import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChange;

import java.io.IOException;
import java.util.List;
import java.util.Collection;
import java.net.URI;
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
public interface Workbook {

    void saveAs(URI uri) throws IOException ;

    void addChangeListener(WorkbookChangeListener changeListener);

    void removeChangeListener(WorkbookChangeListener changeListener);

    Sheet addSheet();

    Sheet addHiddenSheet();

    Sheet addVeryHiddenSheet();

    void deleteSheet(String name);

    List<Sheet> getSheets();

    boolean containsSheet(String name);

    Sheet getSheet(String name);

    Sheet getSheet(int index);

    void addName(String name, Range rng);

    void removeName(String name);

    Collection<NamedRange> getNamedRanges();

    void applyChange(WorkbookChange change);

}
