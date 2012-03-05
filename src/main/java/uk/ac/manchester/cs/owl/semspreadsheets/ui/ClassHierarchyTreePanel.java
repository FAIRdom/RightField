package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;

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
        tabbedPane = new ClassHierarchyTabbedPane(frame.getWorkbookManager());               
        
        add(tabbedPane);
        
    }         
}
