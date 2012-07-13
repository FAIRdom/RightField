/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.listeners;

import uk.ac.manchester.cs.owl.semspreadsheets.model.change.WorkbookChangeEvent;


/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public interface WorkbookChangeListener {

    void workbookChanged(WorkbookChangeEvent event);

    void sheetAdded();

    void sheetRemoved();

    void sheetRenamed(String oldName, String newName);
}
