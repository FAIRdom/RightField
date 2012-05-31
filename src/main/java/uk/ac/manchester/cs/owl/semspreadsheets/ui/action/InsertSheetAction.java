/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 20-Sep-2009
 * 
 * Author: Stuart Owen
 * Date: 04 Oct 2010
 */
@SuppressWarnings("serial")
public class InsertSheetAction extends SpreadSheetAction {

    public InsertSheetAction(WorkbookManager workbookManager, WorkbookFrame workbookFrame) {
        super("Insert", workbookManager, workbookFrame);        
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        Sheet sheet = getSpreadSheetFrame().addSheet();
        getSpreadSheetFrame().setSelectedSheet(sheet);        
    }
}
