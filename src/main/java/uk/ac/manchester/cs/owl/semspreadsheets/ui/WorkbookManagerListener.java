/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
 */
public interface WorkbookManagerListener {

     void workbookCreated(WorkbookManagerEvent event);

    void workbookLoaded(WorkbookManagerEvent event);
    
    void workbookSaved(WorkbookManagerEvent event);

    void ontologiesChanged(WorkbookManagerEvent event);      
    
    void validationAppliedOrCancelled();
        
}
