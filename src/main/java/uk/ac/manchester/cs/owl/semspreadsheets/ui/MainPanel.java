/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/** 
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class MainPanel extends JPanel {

    private WorkbookManager workbookManager;

    private WorkbookPanel workbookPanel;	

    public MainPanel(WorkbookFrame frame) {
        this.workbookManager = frame.getWorkbookManager();
        workbookPanel = new WorkbookPanel(workbookManager);        
        setLayout(new BorderLayout());
        JSplitPane sp = new JSplitPane();
        sp.setLeftComponent(workbookPanel);
        ValidationInspectorPanel validationInspectorPanel = new ValidationInspectorPanel(frame);
        sp.setRightComponent(validationInspectorPanel);
        sp.setResizeWeight(0.9);
        add(sp);
    }            
}
