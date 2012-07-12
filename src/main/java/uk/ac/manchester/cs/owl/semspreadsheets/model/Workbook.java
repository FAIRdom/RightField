/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.WorkbookChange;

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
    
    List<WorkbookChangeListener> getAllChangeListeners();
    
    void clearChangeListeners();

    Sheet addSheet();

    Sheet addHiddenSheet();

    Sheet addVeryHiddenSheet();      

    void deleteSheet(String name);

    List<Sheet> getSheets();
    
    List<Sheet> getVisibleSheets();

    boolean containsSheet(String name);

    Sheet getSheet(String name);

    Sheet getSheet(int index);

    void addName(String name, Range rng);

    void removeName(String name);

    Collection<NamedRange> getNamedRanges();

    void applyChange(WorkbookChange change);

}
