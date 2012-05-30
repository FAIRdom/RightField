/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.change;


/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 01-Nov-2009
 */
public interface WorkbookChangeListener {

    void workbookChanged(WorkbookChangeEvent event);

    void sheetAdded();

    void sheetRemoved();

    void sheetRenamed(String oldName, String newName);
}
