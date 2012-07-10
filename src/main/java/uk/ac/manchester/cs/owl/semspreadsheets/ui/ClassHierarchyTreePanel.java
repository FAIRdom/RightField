/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */

@SuppressWarnings("serial")
public class ClassHierarchyTreePanel extends JPanel {
	
	private ClassHierarchyTabbedPane tabbedPane;	

    public ClassHierarchyTreePanel(WorkbookFrame frame) {
        setLayout(new BorderLayout(5, 5));
        add(new FindClassPanel(frame), BorderLayout.NORTH);
        tabbedPane = new ClassHierarchyTabbedPane(frame);               
        setPreferredSize(new Dimension(100,400));
        add(tabbedPane);        
    }                 
}
